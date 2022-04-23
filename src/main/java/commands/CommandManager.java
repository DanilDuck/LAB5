package commands;

import collection.CollectionManager;
import data.Person;
import exceptions.*;
import file.ReaderWriter;
import io.ConsoleInputManager;
import io.FileInputManager;
import io.InputManager;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

public class CommandManager implements Commandable {
    private static Stack<String> callStack = new Stack<>();
    public boolean Running;
    private Map<String,Command> map;
    private InputManager inputManager;
    private String currentScriptFileName;
    private CollectionManager<Person> collectionManager;
    private HashSet<String> passportId;
    private ReaderWriter fileManager;
    private LinkedList<String> historyCommands = new LinkedList<>();
    public CommandManager(CollectionManager<Person> cManager, InputManager iManager, ReaderWriter fManager){
        Running = false;
        this.inputManager = iManager;
        this.collectionManager = cManager;
        this.fileManager = fManager;
        currentScriptFileName = "";
        map = new HashMap<String,Command>();
        addCommand("info", (a)->System.out.println(collectionManager.getInfo()));
        addCommand("help", (a)->System.out.println(getHelp()));
        addCommand("show", (a)->{
            if (collectionManager.getTreeMap().isEmpty()) System.out.println("collection is empty");
            else {
                TreeSet<Person> treeSet = collectionManager.getTreeMap();
                for (Person p: treeSet) {
                    System.out.print(p.toString());
                }
            }
        });
        addCommand("add", (a)->{
            try {
                collectionManager.add(inputManager.readPerson());
            } catch (InvalidDataException e) {
                e.printStackTrace();
            }
        });
        addCommand("update", (arg)->{
            long id = 0;
            if (arg == null || arg.equals("")){
                throw new MissedCommandArgumentException();
            }
            try{
                id = Long.parseLong(arg);
            } catch (NumberFormatException e){
                throw new InvalidCommandArgumentException("id must be integer");
            }
            if (collectionManager.getTreeMap().isEmpty()) throw new EmptyCollectionException();
            if (!collectionManager.checkID(id)) throw new InvalidCommandArgumentException("no such id");
            try {
                collectionManager.updateID(id, inputManager.readPerson());
            } catch (InvalidDataException e) {
                e.printStackTrace();
            }
        });
        addCommand("load", (arg)->{
            if (!(arg == null ||arg.equals(""))) fileManager.setPath(arg);
            String data = null;
            try {
                data = fileManager.read();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(data.equals("")) throw new CommandException("cannot load, data not found");
            boolean success = collectionManager.deserializeCollection(data);
            passportId = new HashSet<String>();
            for (Person person:collectionManager.getTreeMap()) {
                if(passportId.contains(person.getPassportID())){
                    collectionManager.clear();
                    throw new InvalidNumberException("PassportIds are the same");
                }
                passportId.add(person.getPassportID());
            }
            if(success) System.out.println("collection successfully loaded");
        });
        addCommand("remove_by_id", (arg)->{
            long id = 0;
            if (arg == null || arg.equals("")){
                throw new MissedCommandArgumentException();
            }
            try{
                id = Long.parseLong(arg);
            } catch (NumberFormatException e){
                throw new InvalidCommandArgumentException("id must be integer");
            }
            if (collectionManager.getTreeMap().isEmpty()) throw new EmptyCollectionException();
            if (!collectionManager.checkID(id)) throw new InvalidCommandArgumentException("no such id");
            collectionManager.removeByID(id);
        });
        addCommand("save", (arg)->{
            if (!(arg == null ||arg.equals(""))) fileManager.setPath(arg);
            if (collectionManager.getTreeMap().isEmpty()) System.err.println("collection is empty");
            try {
                if(!fileManager.write(collectionManager.serializeCollection())) throw new CommandException("cannot save collection");
            } catch (IOException e) {
                e.printStackTrace();
            }

        });
        addCommand("clear", (a)->{
            collectionManager.clear();
        });
        addCommand("execute_script",(arg)->{
            if (arg == null || arg.equals("")){
                throw new MissedCommandArgumentException();
            }
            if (callStack.contains(currentScriptFileName)) {
                throw new RecursiveScriptExecuteException();
            }
            callStack.push(currentScriptFileName);
            CommandManager process = new CommandManager(collectionManager, inputManager, fileManager);
            process.fileMode(arg);
            callStack.pop();
            System.out.println("successfully executed script " + arg);
        });
        addCommand("exit", (a)->Running=false);
        addCommand("add_if_max", (arg)-> {
            try {
                int s = Integer.parseInt(arg);
                for (Person person:collectionManager.getTreeMap()) {
                    if(person.getId() >= s){
                        throw new InvalidNumberException();
                    }
                    else{
                        try {
                            collectionManager.addIfMax(inputManager.readPerson());
                        } catch (InvalidDataException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }catch (NumberFormatException | InvalidNumberException e) {
                System.err.println("Wrong number");
            }
        });
        addCommand("filter_contains_name", (arg)->{
            if (arg == null || arg.equals("")){
                throw new MissedCommandArgumentException();
            } else{
                if (collectionManager.getTreeMap().isEmpty()) throw new EmptyCollectionException();
                collectionManager.printContainsName(arg);
            }
        });
        addCommand("history", (a)-> {
                System.out.println(historyCommands);
        });
        addCommand("print_ascending", (a)->{
            if (collectionManager.getTreeMap().isEmpty()) System.out.println("collection is empty");
            else System.out.println(collectionManager.serializeCollection());
        });
        addCommand("print_field_ascending_eye_color", (a)->{
            for (Person p: collectionManager.getTreeMap()) {
                System.out.println(p.getId()+": "+ p.getEyeColor());
            }
        });
        addCommand("print_field_ascending_eye_color", (a)->{
            for (Person p: collectionManager.getTreeMap()) {
                System.out.println(p.getId()+": "+ p.getEyeColor());
            }
        });
        addCommand("remove_greater", (arg)->{
            try {
                int s = Integer.parseInt(arg);
                for (Person person: collectionManager.getTreeMap()) {
                    if(person.getId()>s){
                        collectionManager.removeElement(person);
                    }
                }
            }catch (NumberFormatException e){
                System.err.println("Wrong number");
            }
        });
    }
    public void addCommand(String key, Command c){
        map.put(key, c);
    }
    public boolean hasCommand(String s){
        return map.containsKey(s);
    }
    public void runCommand(String s, String arg){
        try{
            if (!hasCommand(s)) throw new NoSuchCommandException();
            map.get(s).execute(arg);
            historyCommands.push(s);
            if(historyCommands.size()>5){
                historyCommands.removeLast();
            }
        }
        catch(CommandException | InvalidNumberException e){
            System.err.println("Error: "+ e.getMessage());
        }
    }

    public static String getHelp(){
        return "\r\nhelp : show help for available commands\r\n\r\ninfo : Write to standard output information about the collection (type,\r\ninitialization date, number of elements, etc.)\r\n\r\nshow : print to standard output all elements of the collection in\r\nstring representation\r\n\r\nadd {element} : add a new element to the collection\r\n\r\nupdate id {element} : update the value of the collection element whose id\r\nequal to given\r\n\r\nremove_by_id id : remove an element from the collection by its id\r\n\r\nclear : clear the collection\r\n\r\nsave (file_name - optional) : save the collection to a file\r\n\r\nload (file_name - optional): load collection from file\r\n\r\nexecute_script file_name : read and execute script from specified file.\r\nThe script contains commands in the same form in which they are entered\r\nuser is interactive.\r\n\r\nexit : exit the program (without saving to a file)\r\n\r\nremove_greater : remove elements with id greater than\r\n\r\nadd_if_max {element} : add a new element to the collection if its\r\n\r\nfilter_starts_with_name name : output elements, value of field name\r\nwhich starts with the given substring\r\n\r\n";
    }
    public void fileMode(String path){
        currentScriptFileName = path;
        Running = true;
        inputManager = new FileInputManager(path);
        while(Running && inputManager.getScanner().hasNextLine()){
            CommandWrapper pair = inputManager.readCommand();
            runCommand(pair.getCommand(), pair.getArg());
        }
    }
    public void consoleMode(){
        inputManager = new ConsoleInputManager();
        Running = true;
        while(Running){
            System.out.print("enter command (help to get command list): ");
            CommandWrapper pair = inputManager.readCommand();
            runCommand(pair.getCommand(), pair.getArg());
        }
    }
}
