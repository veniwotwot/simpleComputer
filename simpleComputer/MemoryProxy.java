package simpleComputer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class MemoryProxy
{
	private Process process = null;
	private BufferedReader receiver = null;
	private BufferedWriter sender = null;

	public void init(String programFile) throws IOException
	{
		//System.err.println("MemoryProxy_init: Creating Memory Process...");
		//String javaRT = "C:\\Program Files\\Java\\jdk-15.0.1\\bin\\javaw.exe";
		//String memoryClassPaths = "-cp C:\\Users\\Vincent\\eclipse-workspace\\simpleComputer\\bin";
		//String memoryClassFullName = "simpleComputer.MemoryProto";
		
		String javaRT = "java";
		String memoryClassPaths = "-cp /home/011/v/vy/vyw180000/";
		String memoryClassFullName = "simpleComputer/MemoryProto";
		String runCommand = javaRT + " " + memoryClassPaths + " " + memoryClassFullName + " " + programFile;
		//System.err.println(runCommand);
		process = Runtime.getRuntime().exec(runCommand);

		receiver = new BufferedReader(new InputStreamReader(process.getInputStream()));

		sender = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
			
		monitorErrorStream();
	}

	private void monitorErrorStream()
	{
		BufferedReader errorStream = new BufferedReader(new InputStreamReader(process.getErrorStream()));
		new Thread( new Runnable() {
			public void run()
			{
				String message = null;
				try
				{
					while ((message = errorStream.readLine()) != null)
					{
//						System.err.println("MemoryProxy_err: " + message);
					}
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}).start();
	}

	public void destroy() throws Exception
	{
		//System.err.println("MemoryProxy_destroy: exiting...");
		process.destroy();
		Thread.sleep(10);
	}

	public int read(int address) throws Exception
	{
		String message = String.format("READ|%d", address);
		send(message);

		String value = receive();
		return Integer.parseInt(value);
	}

	public void write(int address, int data) throws Exception
	{
		String message = String.format("WRITE|%d|%d", address, data);
		send(message);
		receive();
	}

	public String readline(int address) throws Exception
	{
		String message = String.format("READLINE|%d", address);
		send(message);

		String value = receive();
		return value;
	}

	private void send(String message) throws Exception
	{
		// System.err.println("MemoryProxy_send: " + message);
		sender.write(message + "\n");
		sender.flush();
		Thread.sleep(10);
	}

	private String receive() throws IOException
	{
		String message = null;
		while ((message = receiver.readLine()) != null)
		{
			// System.err.println("MemoryProxy_recv: " + message);
			break;
		}
		return message;
	}
}