package com.jbfvm.core;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Stack;

public class BrainFuckVM implements Runnable {

	private ArrayList<Integer> data = new ArrayList<>();

	private Stack<Long> pc_stack = new Stack<>();

	private PrintStream stdout;

	private PrintStream stderr;

	private InputStream stdin;

	private long pc = 0L;

	private long dc = 0L;

	private long program_len = 0L;

	public BrainFuckVM(byte[] data) {
		init(data, System.out, System.err, System.in);
	}

	public BrainFuckVM(byte[] data, PrintStream stdout, PrintStream stderr, InputStream stdin) {
		init(data, stdout, stderr, stdin);
	}

	private void init(byte[] data, PrintStream stdout, PrintStream stderr, InputStream stdin) {
		if (data == null)
			data = new byte[0];
		for (byte i : data) {
			int t = ((i & 0x80) == 0) ? (i & 0xFF) : ((i & 0x7F) | 0x80);
			if ((i & 0x80) != 0)
				System.out.println("Test: " + t + "; " + i);
			this.data.add(t);
		}
		program_len = data.length;
		dc = program_len;
		this.data.add(0x0);
		this.stdout = stdout;
		this.stderr = stderr;
		this.stdin = stdin;
	}

	public PrintStream getStdOut() {
		return stdout;
	}

	public PrintStream getStdErr() {
		return stderr;
	}

	public InputStream getStdIn() {
		return stdin;
	}

	public long getProgramCounter() {
		return pc;
	}

	public void setDataCounter(long dc) throws IndexOutOfBoundsException {
		this.dc = dc;
		if (dc < 0)
			throw new IndexOutOfBoundsException("Data counter can't be smaller than 0");
		while (dc >= data.size())
			data.add(0x0);
	}

	public long getDataCounter() {
		return dc;
	}

	public int getByte() throws IndexOutOfBoundsException {
		if (dc < 0)
			throw new IndexOutOfBoundsException("Data counter can't be smaller than 0");
		return data.get((int) dc);
	}

	public void setByte(int b) throws IndexOutOfBoundsException {
		if (dc < 0)
			throw new IndexOutOfBoundsException("Data counter can't be smaller than 0");
		data.set((int) dc, b & 0xFF);
	}

	public int getInstruction() throws IndexOutOfBoundsException {
		if (pc < 0)
			throw new IndexOutOfBoundsException("Program counter can't be smaller than 0");
		return data.get((int) pc);
	}

	public long getProgramLength() {
		return program_len;
	}

	public boolean step() throws IndexOutOfBoundsException, IOException {
		boolean ret = false;
		if (pc < program_len) {
			switch (getInstruction()) {
			case 0x3E: // >
				setDataCounter(dc + 1L);
				break;
			case 0x3C: // <
				setDataCounter(dc - 1L);
				break;
			case 0x2B: // +
				setByte(getByte() + 1);
				break;
			case 0x2D: // -
				setByte(getByte() - 1);
				break;
			case 0x5B: // [
				pc_stack.push(pc - 1L);
				break;
			case 0x5D: // ]
				if (getByte() == 0)
					pc_stack.pop();
				else
					pc = pc_stack.pop().intValue();
				break;
			case 0x2E: // .
				if (stdout != null)
					stdout.print((char) getByte());
				break;
			case 0x2C: // ,
				if (stdin != null)
					setByte(stdin.read());
				break;
			}
			++pc;
			ret = true;
		}
		return ret;
	}

	@Override
	public void run() {
		try {
			while (step())
				;
		} catch (IndexOutOfBoundsException | IOException e) {
			if (stderr != null)
				e.printStackTrace(stderr);
		}
	}
}
