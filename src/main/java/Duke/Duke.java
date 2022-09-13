package Duke;

import Duke.Exception.DataCorruptedException;
import Duke.Exception.EmptyArgumentException;
import Duke.Exception.WrongArgumentException;
import Duke.Tasks.TaskType;
import Duke.Tasks.TaskList;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;


public class Duke {
    private static TaskList list = new TaskList();
    private static boolean toSave = true;

    private static final String SEPARATOR = "____________________________________________________________";

    public static void main(String[] args) {
        final String DUKE_LOGO =  " ____        _\n"
                + "|  _ \\ _   _| | _____ \n"
                + "| | | | | | | |/ / _ \\\n"
                + "| |_| | |_| |   <  __/\n"
                + "|____/ \\__,_|_|\\_\\___|\n";
        final String CONV_START = DUKE_LOGO + "Hello! I'm Duke";
        final String CONV_END = "Bye. Hope to see you again soon!";


        printOutput(CONV_START);

        boolean initialiseSuccessful = true;

        try {
            list.initialiseTaskFromFile();
        } catch (FileNotFoundException e) {
            printError("File not found. Make sure the data file is located in './data/data.md'"
            + "\nSaving services disabled.");
            toSave = false;
        } catch (DataCorruptedException e) {
            printError("File data corrupted. Please check data file again or delete entire content."
            + "\nExiting program.");
            initialiseSuccessful = false;
        }

        Scanner in = new Scanner(System.in);

        if (initialiseSuccessful) {
            if (list.getTaskListSize() != 0) {
                printOutput(list.getCompleteList());
            } else {
                try {
                    list.addMarkdownHeader();
                } catch (IOException e) {
                    printError("Something wrong happen, error data: " + e.getMessage());
                }
            }
        }

        while (initialiseSuccessful) {
            System.out.println("What can I do for you?");
            String lineInput = in.nextLine();
            String[] inputSplitted = lineInput.split(" ",2);

            if (inputSplitted[0].equals("bye")) {
                break;
            }

            switch (inputSplitted[0]) {
                case "list":
                    printOutput(list.getCompleteList());
                    break;
                case "mark":
                    try {
                        doMarkAction(inputSplitted[1]);
                    } catch (ArrayIndexOutOfBoundsException | EmptyArgumentException e) {
                        printError("OOPS!!! Please input a number to mark as done.");
                    } catch (WrongArgumentException e) {
                        printError("OOPS, that task is not in the list.");
                    }
                    break;
                case "unmark":
                    try {
                        doUnmarkAction(inputSplitted[1]);
                    } catch (ArrayIndexOutOfBoundsException | EmptyArgumentException e) {
                        printOutput("OOPS!!! Please input a number to unmark as done.");
                    } catch (WrongArgumentException e) {
                        printError("OOPS, that task is not in the list.");
                    }
                    break;
                case "todo":
                    try {
                        doTodoAction(inputSplitted[1]);
                    } catch (ArrayIndexOutOfBoundsException | EmptyArgumentException e){
                        printError("OOPS!!! The description of a todo cannot be empty.");
                    }
                    break;
                case "deadline":
                    try {
                        doDeadlineAction(inputSplitted[1]);
                    } catch (ArrayIndexOutOfBoundsException | EmptyArgumentException e) {
                        printError("OOPS!!! The description of a deadline cannot be empty.");
                    }
                    break;
                case "event":
                    try {
                        doEventAction(inputSplitted[1]);
                    } catch (ArrayIndexOutOfBoundsException | EmptyArgumentException e) {
                        printError("OOPS!!! The description of an event cannot be empty.");
                    }
                    break;
                default:
                    printError("OOPS!!! I'm sorry, but I don't know what that means :-(");
                    break;
            }

        }
        printOutput(CONV_END);
    }

    private static void printOutput(String message) {
        System.out.println(SEPARATOR);
        System.out.println(message);
        System.out.println(SEPARATOR);
        System.out.println("");
    }

    private static void printError(String message) {
        System.out.print("\u001b[31m"); // red font ANSI
        System.out.println(SEPARATOR);
        System.out.println(message);
        System.out.println(SEPARATOR);
        System.out.println("\u001b[0m"); // reset font ANSI
    }

    private static void doMarkAction(String lineInput)
            throws EmptyArgumentException, WrongArgumentException {
        if (lineInput.strip().isEmpty()) {
            throw new EmptyArgumentException();
        }
        int itemNumber = Integer.parseInt(lineInput);
        if (itemNumber > list.getTaskListSize()) {
            throw new WrongArgumentException();
        }

        list.markCompleted(itemNumber, true);
        String message = "Nice! I've marked this task as done:\n"
                + list.getItemFromList(itemNumber);
        printOutput(message);
        if (toSave) {
            try {
                list.updateWholeFile();
            } catch (IOException e) {
                printError("Something went wrong, error data: " + e.getMessage());
            }
        }
    }

    private static void doUnmarkAction(String lineInput)
            throws EmptyArgumentException, WrongArgumentException {
        if (lineInput.strip().isEmpty()) {
            throw new EmptyArgumentException();
        }
        int itemNumber = Integer.parseInt(lineInput);
        if (itemNumber > list.getTaskListSize()) {
            throw new WrongArgumentException();
        }

        list.markCompleted(itemNumber, false);
        String message = "OK, I've marked this task as not done yet:\n"
                + list.getItemFromList(itemNumber);
        printOutput(message);
        if (toSave) {
            try {
                list.updateWholeFile();
            } catch (IOException e) {
                printError("Something went wrong, error data: " + e.getMessage());
            }
        }
    }

    private static void doTodoAction(String lineInput) throws EmptyArgumentException {
        if (lineInput.strip().isEmpty()) {
            throw new EmptyArgumentException();
        }
        int index = list.addTaskToList(lineInput, TaskType.TODO, false, toSave);
        String output = "I got you, added a todo:\n"
                + list.getItemFromList(index + 1)
                + "\n Now you have " + (index + 1) + " tasks in the list.";
        printOutput(output);
    }

    private static void doDeadlineAction(String lineInput) throws EmptyArgumentException {
        if (lineInput.strip().isEmpty()) {
            throw new EmptyArgumentException();
        }
        int index = list.addTaskToList(lineInput, TaskType.DEADLINE, false, toSave);
        String output = "I got you, added a deadline:\n"
                + list.getItemFromList(index + 1)
                + "\n Now you have " + (index + 1) + " tasks in the list.";
        printOutput(output);
    }

    private static void doEventAction(String lineInput) throws EmptyArgumentException {
        if (lineInput.strip().isEmpty()) {
            throw new EmptyArgumentException();
        }
        int index = list.addTaskToList(lineInput, TaskType.EVENT, false, toSave);
        String output = "I got you, added a event:\n"
                + list.getItemFromList(index + 1)
                + "\n Now you have " + (index + 1) + " tasks in the list.";
        printOutput(output);
    }

}