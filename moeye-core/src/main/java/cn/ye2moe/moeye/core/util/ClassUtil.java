package cn.ye2moe.moeye.core.util;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * 扫描 jar包 中的 class 文件
 */
public class ClassUtil {

	public static List<String> getClassList(String pkgName , boolean isRecursive) {
		List<String> classList = new ArrayList();
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		try {
			// 按文件的形式去查找
			String strFile = pkgName.replaceAll("\\.", File.separator);

        	Enumeration<URL> urls = loader.getResources(strFile);

			while (urls.hasMoreElements()) {
				URL url = urls.nextElement();
                if (url != null) {
                	String protocol = url.getProtocol();
                	String pkgPath = URLDecoder.decode(url.getPath(),"UTF-8");
                	//System.out.println("protocol:" + protocol +" path:" + pkgPath);
                    if ("file".equals(protocol)) {
                    	// 本地自己可见的代码
						findClassName(classList, pkgName, pkgPath, isRecursive);
					} else if ("jar".equals(protocol)) {
                    	// 引用第三方jar的代码
						findClassName(classList, pkgName, url, isRecursive);
                    }
                }
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return classList;
	}
	
	public static void findClassName(List<String> clazzList, String pkgName, String pkgPath, boolean isRecursive) {
		if(clazzList == null){
			return;
		}
		File[] files = filterClassFiles(pkgPath);// 过滤出.class文件及文件夹
		//System.out.println("files:" +((files == null)?"null" : "length=" + files.length));
		if(files != null){
			for (File f : files) {
				String fileName = f.getName();
				if (f.isFile()) {
					// .class 文件的情况
					String clazzName = getClassName(pkgName, fileName);
					addClassName(clazzList, clazzName);
				} else {
					// 文件夹的情况
					if(isRecursive){
						// 需要继续查找该文件夹/包名下的类
						String subPkgName = pkgName +"."+ fileName;
						String subPkgPath = pkgPath +"/"+ fileName;
						findClassName(clazzList, subPkgName, subPkgPath, true);
					}
				}
			}
		}
	}
	
	/**
	 * 第三方Jar类库的引用。<br/>
	 * @throws IOException 
	 * */
	public static void findClassName(List<String> clazzList, String pkgName, URL url, boolean isRecursive) throws IOException {
		JarURLConnection jarURLConnection = (JarURLConnection) url.openConnection();
		JarFile jarFile = jarURLConnection.getJarFile();
		//System.out.println("jarFile:" + jarFile.getName());
		Enumeration<JarEntry> jarEntries = jarFile.entries();
		while (jarEntries.hasMoreElements()) {
			JarEntry jarEntry = jarEntries.nextElement();
			String jarEntryName = jarEntry.getName(); // 类似：sun/security/internal/interfaces/TlsMasterSecret.class
			String clazzName = jarEntryName.replace("/", ".");
			int endIndex = clazzName.lastIndexOf(".");
			String prefix = null;
			if (endIndex > 0) {
				String prefix_name = clazzName.substring(0, endIndex);
				endIndex = prefix_name.lastIndexOf(".");
				if(endIndex > 0){
					prefix = prefix_name.substring(0, endIndex);
				}
			}
			if (prefix != null && jarEntryName.endsWith(".class")) {
//				System.out.println("prefix:" + prefix +" pkgName:" + pkgName);
				if(prefix.equals(pkgName)){
					//System.out.println("jar entryName:" + jarEntryName);
					addClassName(clazzList, clazzName);
				} else if(isRecursive && prefix.startsWith(pkgName)){
					// 遍历子包名：子类
					//System.out.println("jar entryName:" + jarEntryName +" isRecursive:" + isRecursive);
					addClassName(clazzList, clazzName);
				}
			}
		}
	}
	
	private static File[] filterClassFiles(String pkgPath) {
		if(pkgPath == null){
			return null;
		}
		// 接收 .class 文件 或 类文件夹
		return new File(pkgPath).listFiles(new FileFilter() {
			@Override
			public boolean accept(File file) {
				return (file.isFile() && file.getName().endsWith(".class")) || file.isDirectory();
			}
		});
    }
	
	private static String getClassName(String pkgName, String fileName) {
		int endIndex = fileName.lastIndexOf(".");
		String clazz = null;
		if (endIndex >= 0) {
			clazz = fileName.substring(0, endIndex);
		}
		String clazzName = null;
		if (clazz != null) {
			clazzName = pkgName + "." + clazz;
		}
		return clazzName;
	}
	
	private static void addClassName(List<String> clazzList, String clazzName) {
		if(clazzName.contains("$")||clazzName.contains("HttpServer"))
			return;
		//System.out.println("**********************"+clazzName);
		clazzList.add(clazzName.replace(".class",""));
	}
}