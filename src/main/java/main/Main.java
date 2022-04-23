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
 * @author Danil
 * @version 1.1
 *
 */
public class Main {
    public static void main(String[] args) throws UnsupportedEncodingException {
        FileManager fileManager = new FileManager("C:\\Users\\imia\\IdeaProjects\\file.json");
        PersonCollectionManager collectionManager = new PersonCollectionManager();
        InputManager consoleManager = new ConsoleInputManager();
        CommandManager commandManager = new CommandManager(collectionManager,consoleManager,fileManager);
        commandManager.consoleMode();
    }
}
