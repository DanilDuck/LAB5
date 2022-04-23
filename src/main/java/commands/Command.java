package commands;

import exceptions.InvalidNumberException;
/**
 * Command callback interface
 */
@FunctionalInterface
public interface Command {
    void execute(String arg) throws InvalidNumberException;
}
