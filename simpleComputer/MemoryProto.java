package simpleComputer;

import java.io.File;
import java.util.Scanner;

/*

int[2000]

      system memory
1999:               <- system stack grows down           

1000:

      user program
 999:               <- User Stack grows down
 
   9: 50
   8: 14
   7: 9
   6: 11
   5: 10
   4: 8 
   3: 16
   2: 8
   1: 14
   0: 8             <- Program Counter (PC)

*/

public class MemoryProto
{
	// array of integers representing memory storage
	private int[] storage = new int[2000];
	private String[] lines = new String[2000];

	public static void main(String[] args)
	{
		try
		{
			MemoryProto memory = new MemoryProto();
			memory.init(args.length > 0 ? args[0] : null);
			memory.process();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private void process() throws Exception
	{
		Scanner input = new Scanner(System.in);
		while (input.hasNextLine())
		{
			String line = input.nextLine();
			String[] tokens = line.split("\\|");	//Ignore all input after the comment

			String output = handle(tokens);
			System.out.println(output);

			if (line.equals("exit"))
				break;
		}
		input.close();
	}

	//handles the "response" for debugging and calling next method
	private String handle(String[] tokens)
	{
		String response;
		String action = tokens[0];
		switch (action)
		{
		case "WRITE":
			int writeAddress = Integer.parseInt(tokens[1]);
			int writeValue = Integer.parseInt(tokens[2]);
			write(writeAddress, writeValue);
			response = "Write OK. " + writeAddress + ", " + writeValue;
			break;

		case "READ":
			int readAddress = Integer.parseInt(tokens[1]);
			int readValue = read(readAddress);
			// response = "Read OK. " + readAddress + ", " + readValue;
			response = String.valueOf(readValue);
			break;

		case "READLINE":
			response = readline(Integer.parseInt(tokens[1]));
			break;

		default:
			String defaultRespone = String.join(",", tokens);
			response = "Unkown request ... " + defaultRespone + ". OK.";
		}

		return response;
	}

	//basic read
	public int read(int address)
	{
		int data = storage[address];
		return data;
	}

	//basic write
	public void write(int address, int data)
	{
		storage[address] = data;
	}

	public String readline(int address)
	{
		return String.format("[%4d]: %3d   # %s",
				address,
				storage[address],
				lines[address]);
	}

	//debugging
	private void printLoadline(int address)
	{
		System.err.println("program loader: " + readline(address));
	}

	//debugging
	private void printNoLoad(String command)
	{
		System.err.println("program loader:               # " + command);
	}

	public void init(String fileName)
	{
		try
		{
			// Initialize Memory with all 0's
			for (int i : storage)
			{
				write(i, 0);
			}

			if (fileName == null)
			{
				System.err.println("program loader skipped");
				return;
			}

			Scanner file = new Scanner(new File(fileName));
			// Read in
			int address = 0;
			while (file.hasNextLine())
			{
				String line = file.nextLine();
				if (line.isEmpty())
				{
					printNoLoad(line);
					continue;
				}

				String command = line.trim();
				if (!command.substring(0, 1).equals("."))
				{
					try
					{
						String[] tokens = command.split(" ", 2);
						storage[address] = Integer.parseInt(tokens[0]);
						lines[address] = command;
						printLoadline(address);
					}
					catch (NumberFormatException e)
					{
						printNoLoad(line);
						continue;
					}
				}
				else
				{
					String[] tokens = command.substring(1).split(" ", 2);
					address = Integer.parseInt(tokens[0]) - 1;				
					printNoLoad(line);
				}
				address++;
			}
		}
		catch (Exception e)
		{
			// System.err.println("File not found");
			System.out.println(e);
			System.exit(0);
		}
	}

	public void destroy()
	{

	}

}
