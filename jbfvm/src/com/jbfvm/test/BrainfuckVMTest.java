package com.jbfvm.test;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import org.junit.Before;
import org.junit.Test;

import com.jbfvm.core.BrainfuckVM;

/**
 * Brainfuck VM test class
 * 
 * @author Ethem Kurt
 *
 */
public class BrainfuckVMTest {

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
	@Before
	public void setUp() throws Exception {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int n;
		byte[] data = new byte[1024];
		try (FileInputStream fis = new FileInputStream("mandelbrot.bf")) {
			while ((n = fis.read(data)) != -1)
				baos.write(data, 0, n);
		} finally {
			//
		}
		bfvm = new BrainfuckVM(baos.toByteArray());
	}

	/**
	 * Test
	 */
	@Test
	public void test() {
		bfvm.run();
	}

}
