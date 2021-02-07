package com.lxh.util;

/**
 * Created by lxh on 2018/5/12.
 */

import lombok.Data;

import java.io.*;

/**
 * @author lxh
 * @Date 2018/5/12
 */
@Data
public class MyClassLoader extends ClassLoader {

	private String path = "/Users/lxh/Downloads/test/cl"; // 默认加载路径

	private String name; // 类加载器名称

	private final String filetype = ".class"; // 文件类型

	public MyClassLoader(String name) {
		// TODO Auto-generated constructor stub
		super();
		this.name = name;
	}

	public MyClassLoader(ClassLoader parent, String name) {
		super(parent);
		this.name = name;
	}

	@Override
	public Class<?> findClass(String name) throws ClassNotFoundException {
		// TODO Auto-generated method stub
		byte[] b = loadClassData(name);
		return defineClass(name, b, 0, b.length);
	}

	private byte[] loadClassData(String name) {
		byte[] data = null;
		InputStream in = null;
		name = name.replace('.', '/');
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			in = new FileInputStream(new File(path + name + filetype));
			int len = 0;
			while (-1 != (len = in.read())) {
				out.write(len);
			}
			data = out.toByteArray();
		}
		catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally {
			try {
				in.close();
				out.close();
			}
			catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return data;
	}


	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return this.name;
	}

}
