package com.bigeti.jbfvm;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Stack;

/**
 * Brainfuck VM class
 *
 * @author Ethem Kurt
 */
public class BrainfuckVM implements Runnable
{

	/**
	 * Program and data
	 */
	private final ArrayList<Byte> data = new ArrayList<>();

	/**
	 * Program counter stack
	 */
	private final Stack<Integer> pc_stack = new Stack<>();

	/**
	 * Standard output
	 */
	private PrintStream stdout = System.out;

	/**
	 * Standard error output
	 */
	private PrintStream stderr = System.err;

	/**
	 * Standard input
	 */
	private InputStream stdin = System.in;

	/**
	 * Program counter
	 */
	private int pc = 0;

	/**
	 * Pointer
	 */
	private int p = 0;

	/**
	 * Program length
	 */
	private int program_len = 0;

	/**
	 * Constructor
	 *
	 * @param program
	 *            Program
	 */
	public BrainfuckVM(final byte[] program)
	{
		init(program, null, null, null);
	}

	/**
	 * Constructor
	 *
	 * @param program
	 *            Program
	 * @param stdout
	 *            Standard output
	 * @param stderr
	 *            Standard error output
	 * @param stdin
	 *            Standard input
	 */
	public BrainfuckVM(final byte[] program, final PrintStream stdout, final PrintStream stderr, final InputStream stdin)
	{
		init(program, stdout, stderr, stdin);
	}

	/**
	 * Initialize VM
	 *
	 * @param program
	 *            Program
	 * @param stdOut
	 *            Standard output
	 * @param stdErr
	 *            Standard error output
	 * @param stdIn
	 *            Standard input
	 */
	private void init(byte[] program, final PrintStream stdOut, final PrintStream stdErr, final InputStream stdIn)
	{
		byte[] prog = (program == null) ? new byte[0] : program;
		for (final byte i : prog)
		{
			data.add(i);
		}
		program_len = prog.length;
		p = program_len;
		data.add((byte) 0x0);
		if (stdOut != null)
		{
			this.stdout = stdOut;
		}
		if (stdErr != null)
		{
			this.stderr = stdErr;
		}
		if (stdIn != null)
		{
			this.stdin = stdIn;
		}
	}

	/**
	 * Get standard output
	 *
	 * @return Standard output
	 */
	public PrintStream getStdOut()
	{
		return stdout;
	}

	/**
	 * Get standard error output
	 *
	 * @return Standard error output
	 */
	public PrintStream getStdErr()
	{
		return stderr;
	}

	/**
	 * Get standard input
	 *
	 * @return Standard input
	 */
	public InputStream getStdIn()
	{
		return stdin;
	}

	/**
	 * Get program counter
	 *
	 * @return Program counter
	 */
	public long getProgramCounter()
	{
		return pc;
	}

	/**
	 * Increment pointer
	 *
	 * @throws IndexOutOfBoundsException
	 */
	private void incrementPointer() throws IndexOutOfBoundsException
	{
		++p;
		if (p < 0)
		{
			throw new IndexOutOfBoundsException("Pointer can't be less than 0");
		}
		while (p >= data.size())
		{
			data.add((byte) 0x0);
		}
	}

	/**
	 * Decrement pointer
	 *
	 * @throws IndexOutOfBoundsException
	 */
	private void decrementPointer() throws IndexOutOfBoundsException
	{
		--p;
		if (p < 0)
		{
			throw new IndexOutOfBoundsException("Pointer can't be less than 0");
		}
		while (p >= data.size())
		{
			data.add((byte) 0x0);
		}
	}

	/**
	 * Get pointer
	 *
	 * @return Pointer
	 */
	public int getPointer()
	{
		return p;
	}

	/**
	 * Get byte
	 *
	 * @return Byte
	 * @throws IndexOutOfBoundsException
	 *             Invalid pointer access
	 */
	public byte getByte() throws IndexOutOfBoundsException
	{
		if (p < 0)
		{
			throw new IndexOutOfBoundsException("Pointer can't be less than 0");
		}
		return data.get(p);
	}

	/**
	 * Set byte
	 *
	 * @param b
	 *            Byte
	 * @throws IndexOutOfBoundsException
	 *             Invalid pointer access
	 */
	private void setByte(final byte b) throws IndexOutOfBoundsException
	{
		if (p < 0)
		{
			throw new IndexOutOfBoundsException("Pointer can't be less than 0");
		}
		data.set(p, b);
	}

	/**
	 * Increment byte
	 *
	 * @throws IndexOutOfBoundsException
	 *             Invalid pointer access
	 */
	private void incrementByte() throws IndexOutOfBoundsException
	{
		setByte((byte) ((getByte() & 0xFF) + 1));
	}

	/**
	 * Decrement byte
	 *
	 * @throws IndexOutOfBoundsException
	 *             Invalid pointer access
	 */
	private void decrementByte() throws IndexOutOfBoundsException
	{
		setByte((byte) ((getByte() & 0xFF) - 1));
	}

	/**
	 * Get instruction
	 *
	 * @return Instruction
	 * @throws IndexOutOfBoundsException
	 *             Invalid program counter access
	 */
	public byte getInstruction() throws IndexOutOfBoundsException
	{
		if (pc < 0)
		{
			throw new IndexOutOfBoundsException("Program counter can't be smaller than 0");
		}
		return data.get(pc);
	}

	/**
	 * Get program length
	 *
	 * @return Program length
	 */
	public int getProgramLength()
	{
		return program_len;
	}

	/**
	 * Instruction step
	 *
	 * @return "true" if not terminated, otherwise "false"
	 * @throws IndexOutOfBoundsException
	 *             Invalid pointer or program counter access
	 * @throws IOException
	 *             Program error
	 */
	public boolean step() throws IndexOutOfBoundsException, IOException
	{
		boolean ret = false;
		if (pc < program_len)
		{
			switch (getInstruction())
			{
				case 0x3E: // >
					incrementPointer();
					break;
				case 0x3C: // <
					decrementPointer();
					break;
				case 0x2B: // +
					incrementByte();
					break;
				case 0x2D: // -
					decrementByte();
					break;
				case 0x5B: // [
					if (getByte() == 0)
					{
						int brackets = 0;
						while (pc < program_len)
						{
							switch (getInstruction())
							{
								case 0x5B: // [
									++brackets;
									break;
								case 0x5D: // ]
									--brackets;
									break;
							}
							if (brackets == 0)
							{
								break;
							}
							++pc;
						}
						if (brackets > 0)
						{
							throw new IOException("Invalid brackets detected");
						}
					}
					else
					{
						pc_stack.push(pc - 1);
					}
					break;
				case 0x5D: // ]
					pc = pc_stack.pop().intValue();
					break;
				case 0x2E: // .
					if (stdout != null)
					{
						stdout.print((char) getByte());
					}
					break;
				case 0x2C: // ,
					if (stdin != null)
					{
						setByte((byte) stdin.read());
					}
					break;
			}
			++pc;
			ret = true;
		}
		return ret;
	}

	@Override
	public void run()
	{
		try
		{
			while (step())
			{
				// Do nothing
			}
		}
		catch (IndexOutOfBoundsException | IOException e)
		{
			if (stderr != null)
			{
				e.printStackTrace(stderr);
			}
		}
	}
}
