package com.jbfvm.test;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import org.junit.Before;
import org.junit.Test;

import com.jbfvm.core.BrainFuckVM;

public class BrainFuckVMTest {

	private BrainFuckVM bfvm;

	@Before
	public void setUp() throws Exception {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int n;
		byte[] data = new byte[100000];
		try (FileInputStream fis = new FileInputStream("mandelbrot.bf")) {
			while ((n = fis.read(data)) != -1)
				baos.write(data, 0, n);
		} finally {
			//
		}
		bfvm = new BrainFuckVM(baos.toByteArray());
	}

	@Test
	public void test() throws InterruptedException {
		/*Thread t = new Thread(bfvm);
		t.start();
		t.join();*/
		bfvm.run();
	}

}
