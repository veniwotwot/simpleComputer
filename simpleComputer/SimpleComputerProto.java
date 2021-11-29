package simpleComputer;

import java.io.IOException;

// Registers
// PC  - Program counter. It contains the memory address of the next instruction to be fetched
// IR  - Instruction Register. holds the instruction to be executed.
//       The instruction from PC is fetched and stored in IR.
//       As soon as the instruction in placed in IR, the CPU starts executing the instruction and the PC points to the next instruction to be executed.
// SP  - Stack pointer. stores the address of the last program request in a stack
// AC  - Accumulator. Store data taken from memory
// X   - X
// Y   - Y

public class SimpleComputerProto
{
	private int x = 0;
	private int y = 0;
	private int pc = 0;
	private int ir = 0;
	private int ac = 0;
	private int timer = 0;
	private int sp = 1000;
	final static int memBoundary = 1000;
	private boolean userMode = true;
	private boolean exit = false;
	private final int instructionOP[] = { 1, 2, 3, 4, 5, 7, 9, 20, 21, 22, 23 };
	public static int time = 0;

	private MemoryProxy memoryManager = new MemoryProxy();
//	private MemoryProto memoryManager = new MemoryProto();

	public static void main(String[] args)
	{
		try
		{
			 String programFile = args[0];
			// String programPath = "C:\\Users\\Vincent\\eclipse-workspace\\simpleComputer\\bin\\simpleComputer";
			// String programFile = programPath + "\\samplePrintHi.txt";
			// String programFile = programPath + "\\sample1.txt";
			// String programFile = programPath + "\\sample2.txt";
			// String programFile = programPath + "\\sample3.txt";
			// String programFile = programPath + "\\sample4.txt";
			// String programFile = programPath + "\\sample5.txt";
			//System.out.println(programFile);

			 time = Integer.parseInt(args[1]); // contains the second command line
			// argument which is the timer
			//time = Integer.parseInt("30"); // contains the second command line argument which is the timer
			//System.out.println("Time: " + time);

			SimpleComputerProto computer = new SimpleComputerProto();
			computer.bootUp(programFile);

			// computer.debugMemory(0, 10);
			// computer.debugReadWrite();
			computer.run();

			computer.shutdown();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private void shutdown() throws Exception
	{
		memoryManager.destroy();
	}

	private void bootUp(String programFile) throws IOException
	{
		memoryManager.init(programFile);
	}

	private void debugMemory(int beginIdx, int endIdx) throws Exception
	{
		for (int i = beginIdx; i < endIdx; i++)
		{
			System.out.println("Memory Dump: " + memoryManager.readline(i));
		}
	}

	private void debugReadWrite() throws Exception
	{
		int result;

		memoryManager.write(5, 17);
		result = memoryManager.read(5);
		System.out.println("Memory read: " + result);

		memoryManager.write(5, 25);
		result = memoryManager.read(5);
		System.out.println("Memory read: " + result);

		debugMemory(0, 10);
	}

	private void debugRegister()
	{
		debugPrintLn(String.format(
				"Registers: PC:%3d, IR:%3d, AC:%4d, X:%3d, Y:%3d, SP:%4d", pc, ir, ac, x, y, sp));
	}

	//Uncomment this err.print line if debugging.
	private void debugPrintLn(String message)
	{
//		System.err.printf(message + "\n");
	}

	private void run() throws Exception
	{
		int count = 0;
		while (true)
		{
			debugPrintLn("Cycle: " + count);
			cycle(); // keeps executing this method so until exit is false.
			if (exit)
			{
				break;
			}
			count++;
		}
	}

	private void cycle() throws Exception
	{
		boolean flagOP = false;
		ir = read(this.pc); // reads the Instruction from PC and stores in IR
		debugRegister();

		// Checks whether the value at PC is an instruction which requires OPCode
		for (int i = 0; i < instructionOP.length; i++)
		{
			if (ir == instructionOP[i])
			{
				flagOP = true; // OPCode needed
			}
		}

		if (flagOP)
		{
			int op = read(++this.pc); // reads the OPCODE, kept in next line
			boundaryCheck(op); // checking for Memory Access Violations.
			processInstruction(ir, op);
		}
		else
		{
			processInstruction(ir, 0); // Sending 0 as a default since no Opcode is required
		}
		this.pc++; // increments PC after every instruction is executed
	}

	// This is the instruction method. It contains all the instructions that the CPU
	// can execute
	private void processInstruction(int instruction, int op) throws Exception
	{
		switch (instruction)
		{
		case 1: // Load the value into the AC
			this.ac = op;
			break;

		case 2: // Load the value at the address into the AC
			boundaryCheck(op);
			this.ac = read(op);
			break;

		case 3: // Load the value from the address found in the given address into the AC
			boundaryCheck(op);
			boundaryCheck(read(op));
			this.ac = read(read(op));
			break;

		case 4: // Load the value at (address+X) into the AC
			boundaryCheck(op + this.x);
			this.ac = read(op + this.x);
			break;

		case 5: // Load the value at (address+Y) into the AC
			boundaryCheck(op + this.y);
			this.ac = read(op + this.y);
			break;

		case 6: // Load from (Sp+X) into the AC (if SP is 990, and X is 1, load from 991).
			boundaryCheck(this.sp + this.x);
			this.ac = read(this.sp + this.x);
			break;

		case 7: // Store the value in the AC into the address
			boundaryCheck(op);
			write(op, this.ac);
			break;

		case 8: // Gets a random int from 1 to 100 into the AC
			this.ac = (int) (Math.random() * (100) + 1);
			break;

		case 9: // If port=1, writes AC as an int to the screen
				// If port=2, writes AC as a char to the screen
			if (op == 1)
				System.out.print(ac);
			else if (op == 2)
				System.out.print((char) ac);
			break;

		case 10: // Add the value in X to the AC
			this.ac += this.x;
			break;

		case 11: // Add the value in Y to the AC
			this.ac += this.y;
			break;

		case 12: // Subtract the value in X from the AC
			this.ac -= this.x;
			break;

		case 13: // Subtract the value in Y from the AC
			this.ac -= this.y;
			break;

		case 14: // Copy the value in the AC to X
			this.x = this.ac;
			break;

		case 15: // Copy the value in X to the AC
			this.ac = this.x;
			break;

		case 16: // Copy the value in the AC to Y
			this.y = this.ac;
			break;

		case 17: // Copy the value in Y to the AC
			this.ac = this.y;
			break;

		case 18: // Copy the value in AC to the SP
			this.sp = this.ac;
			break;

		case 19: // Copy the value in SP to the AC 
			this.ac = this.sp;
			break;

		case 20: // Jump to the address
			this.pc = op - 1; // increments after rest of cycle() is executed
			break;

		case 21: // Jump to the address only if the value in the AC is zero
			if (this.ac == 0)
			{
				this.pc = op - 1; // increments after rest of cycle() is executed
			}
			break;

		case 22: // Jump to the address only if the value in the AC is not zero
			if (this.ac != 0)
			{
				this.pc = op - 1; // increments after rest of cycle() is executed
			}
			break;

		case 23: // Push return address onto stack, jump to the address
			this.sp--;
			write(this.sp, ++this.pc);
			this.pc = op - 1; // increments after rest of cycle() is executed
			break;

		case 24: // Pop return address from the stack, jump to the address
			this.pc = read(this.sp) - 1;
			this.sp++;
			break;

		case 25: // Increment the value in X
			this.x++;
			break;

		case 26: // Decrement the value in X
			this.x--;
			break;

		case 27: // Push AC into stack
			this.sp--;
			write(this.sp, this.ac);
			break;

		case 28: // Pop from stack into AC
			this.ac = read(this.sp);
			this.sp++;
			break;

		case 29: // Interrupt code (Set to system Mode) Perform System Call
					// Push registers to stack
					// Set new SP and PC
			if (userMode)
			{
				userMode = false;
				write(1999, this.sp);
				write(1998, ++this.pc);
				this.sp = 1997;
				this.pc = 1499; // 1499 + 1 = 1500
			}
			break;

		case 30: // Set to user mode 	Return from system call
				 // Read stack registers
			if (sp < 2000)
			{
				this.pc = read(1998) - 1;
				this.sp = read(1999);
				userMode = true;
			}
			else
			{
				System.out.println("Error: System Stack Empty");
				shutdown();
				System.exit(1);
			}
			break;

		case 50: // End execution
			exit = true; // CPU exits cycle()
			break;

		default: // If invalid instruction
			System.err.println("Invalid Instruction: PC-" + pc + "\nIR-" + ir);
			System.exit(0);
			;
			break;
		}

		timer++; // increments Timer after every instruction

		// If Timer is reached, interrupt and set to system mode
		// Copy registers to stack
		// Set new SP and PC = (1000)
		if (timer == time)
		{
			if (userMode)
			{
				write(1999, this.sp);
				write(1998, ++this.pc);
				this.sp = 1998;
				this.pc = 999; // 999 + 1 = 1000
				userMode = false;
				debugPrintLn("*** Timer PC " + read(pc+1));
			}
			timer = 0;
		}
	}

	// Checks Memory Access Ciolations if the CPU is in user mode
	private void boundaryCheck(int address)
	{
		debugPrintLn("checking memory of: " + address);
		if (userMode)
		{
			if (address < 0 || address >= memBoundary)
			{
				System.err.println("Access Memory Denied");
				System.exit(0);
			}
		}
	}

	// Sends request to the pipe, read address from Memory
	private int read(int address) throws Exception
	{
		return memoryManager.read(address);
	}

	// Send request to write the data in Memory
	private void write(int address, int value) throws Exception
	{
		memoryManager.write(address, value);
	}
}
