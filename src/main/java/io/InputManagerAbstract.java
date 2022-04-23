package io;

import collection.CollectionManager;
import commands.CommandWrapper;
import data.*;
import exceptions.EmptyStringException;
import exceptions.InvalidDataException;
import exceptions.InvalidEnumException;
import exceptions.InvalidNumberException;


import java.time.LocalDate;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;
/**
 * basic abstract of InputManager
 */
public abstract class InputManagerAbstract implements InputManager{
    private Scanner scanner;
    private HashSet<String> passportId;
    public InputManagerAbstract(Scanner scanner1){
        scanner = scanner1;
        scanner.useDelimiter("\n");
    }
    public Scanner getScanner(){
        return scanner;
    }
    public void setScanner(Scanner sc){
        scanner = sc;
    }
    public String readName() throws EmptyStringException {
        String s = scanner.nextLine().trim();
        if (s.equals("")){
            throw new EmptyStringException();
        }
        return s;
    }
    public int readXCoord() throws InvalidNumberException {
        int x;
        try {
            x = Integer.parseInt(scanner.nextLine());
        }catch(NumberFormatException e){
            throw new InvalidNumberException();
        }
        return x;
    }
    public float readYCoord() throws InvalidNumberException{
        Float y;
        try{
            y = Float.parseFloat(scanner.nextLine());
        }
        catch(NumberFormatException e){
            throw new InvalidNumberException();
        }
        return y;
    }
    public Coordinates readCoords() throws InvalidNumberException{
        int x = readXCoord();
        float y = readYCoord();
        Coordinates coord = new Coordinates(x,y);
        return coord;
    }
    public float readHeight() throws InvalidNumberException{
        Float s;
        try{
            s = Float.parseFloat(scanner.nextLine().trim());
        }
        catch(NumberFormatException e){
            throw new InvalidNumberException();
        }

        if(s<0) throw new InvalidNumberException("must be greater than 0");
        return s;
    }
    public Color readEyeColor() throws InvalidEnumException {
        String s = scanner.nextLine().trim();
        if(s.equals("")){
            return null;
        }
        else {
            try{
                return Color.valueOf(s);
            } catch(IllegalArgumentException e){
                throw new InvalidEnumException();
            }
        }
    }
    public Country readCountry() throws InvalidEnumException {
        String s = scanner.nextLine().trim();
        if(s.equals("")){
            return null;
        }
        else {
            try{
                return Country.valueOf(s);
            } catch(IllegalArgumentException e){
                throw new InvalidEnumException();
            }
        }
    }
    public String readNationality() throws EmptyStringException {
        String s = scanner.nextLine().trim();
        if (s.equals("")) {
            throw new EmptyStringException();
        }
        return s;
    }
    public String readPassportID() throws InvalidNumberException{
        String y;
        y = scanner.nextLine();
        if(passportId == null) passportId = new HashSet<String>();
        else{
            for (String passport: passportId) {
                if(passport.equals(y)){
                    throw new InvalidNumberException("This passportID already exists");
                }
            }
        }
        if (y.length() < 10){
            throw new InvalidNumberException("Passport >=10");
        }
        else{
            passportId.add(y);
        }
        return y;
    }
    public void setPassportId(HashSet<String> passportId){
        this.passportId = passportId;
    }
    public Integer readXLocation() throws InvalidNumberException{
        Integer x;
        try{
            x = Integer.parseInt(scanner.nextLine());
        }
        catch(NumberFormatException e){
            throw new InvalidNumberException();
        }
        return x;
    }
    public double readYLocation() throws InvalidNumberException{
        double y;
        try{
            y = Double.parseDouble(scanner.nextLine());
        }
        catch(NumberFormatException e){
            throw new InvalidNumberException();
        }
        return y;
    }

    public int readZLocation() throws InvalidNumberException{
        Integer z;
        try{
            z = Integer.parseInt(scanner.nextLine());
        }
        catch(NumberFormatException e){
            throw new InvalidNumberException();
        }
        return z;
    }
    public Location readLocation() throws InvalidNumberException {
        Integer x = readXLocation();
        Double y = readYLocation();
        Integer z = readZLocation();
        Location location = new Location(x,y,z);
        return location;
    }
    public Person readPerson() throws InvalidDataException {
        Person person = null;
        String name = readName();
        Coordinates coords = readCoords();
        Float height = readHeight();
        String passportID = readPassportID();
        Color color = readEyeColor();
        Country country = readCountry();
        Location location = readLocation();
        person = new Person(name, coords, height, passportID, color, country,location);
        return person;

    }
    public CommandWrapper readCommand(){
        String cmd = scanner.nextLine();
        if (cmd.contains(" ")){ //if command has argument
            String arr [] = cmd.split(" ",2);
            cmd = arr[0];
            String arg = arr[1];
            return new CommandWrapper(cmd,arg);
        } else {
            return new CommandWrapper(cmd);
        }
    }







}
