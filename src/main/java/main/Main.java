package main;

import collection.CollectionManager;
import collection.PersonCollectionManager;
import commands.CommandManager;
import data.Person;
import file.FileManager;
import io.ConsoleInputManager;
import io.InputManager;

import java.io.UnsupportedEncodingException;
/**
 * @author Daniil
 * @version 1.1
 *
 */
public class Main {
    public static void main(String[] args) throws UnsupportedEncodingException {
        FileManager fileManager = new FileManager();
        PersonCollectionManager collectionManager = new PersonCollectionManager();
        if(args.length !=0){
            fileManager.setPath(args[0]);
            collectionManager.deserializeCollection(fileManager.read());
        }
        else{
            System.out.println("no file passed by argument");
        }
        InputManager consoleManager = new ConsoleInputManager();
        CommandManager commandManager = new CommandManager(collectionManager,consoleManager,fileManager);
        commandManager.consoleMode();
    }
}
