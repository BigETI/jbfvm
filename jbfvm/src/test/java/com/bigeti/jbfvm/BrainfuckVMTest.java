package com.bigeti.jbfvm;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * Brainfuck VM test class
 *
 * @author Ethem Kurt
 */
public class BrainfuckVMTest
{

	/**
	 * Brainfuck VM
	 */
	private BrainfuckVM bfvm;

	/**
	 * Set up
	 *
	 * @throws Exception
	 *             Set up exception
	 */
	@BeforeAll
	public void setUp() throws Exception
	{
		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int n;
		final byte[] data = new byte[1024];
		try (FileInputStream fis = new FileInputStream(BrainfuckVMTest.class.getResource("/com/bigeti/jbfvm/mandelbrot.bf").toExternalForm()))
		{
			while ((n = fis.read(data)) != -1)
			{
				baos.write(data, 0, n);
			}
		}
		bfvm = new BrainfuckVM(baos.toByteArray());
y	}

	/**
	 * Test
	 */
	@Test
	public void test()
	{
		bfvm.run();
	}

}
