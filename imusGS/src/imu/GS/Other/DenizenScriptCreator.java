package imu.GS.Other;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class DenizenScriptCreator 
{
	private String _nameTag="gs_assignments_shops.dsc";
	private String _path = "";
	
	ArrayList<String> example_script;
	String str_script_name = null;
	String cmd = "gs open shop ";
	public DenizenScriptCreator() 
	{
		_path = "plugins/Denizen/scripts/"+_nameTag;
		example_script = ReadDenizenTemplate();
		
	}
	
	public String CreateAssignScript(String script_name, String shopname)
	{
		WriteTheNewScript(script_name, CoverLines(script_name,shopname));		
		return str_script_name;
	}
	
	ArrayList<String> CoverLines(String script_name, String shopname)
	{
		ArrayList<String> lines_for_new = new ArrayList<>();
		if(example_script == null || example_script.isEmpty())
		{
			System.out.println("couldnt get that");
			return null;
		}
		
		str_script_name = null;
		
		for(String line : example_script)
		{
			String mod_str = line;
			if(line.contains("generalstore"))
			{
				mod_str = mod_str.replace("generalstore", script_name);
				if(str_script_name == null)
				{
					str_script_name = mod_str.replace(":", "");
				}
			}
			
			if(line.contains("gs shop"))
			{
				mod_str = mod_str.replace("gs shop", cmd+shopname);
			}
			lines_for_new.add(mod_str);
		}
		return lines_for_new;
	}
	
	void WriteTheNewScript(String fileName, ArrayList<String> lines)
	{
		try 
		{
			//System.out.println("try to find: "+_main.getDataFolder()+"/Denizen/scripts/"+script_name+".dc");
			
			File f = new File(_path);
			BufferedWriter bw;
			if(!f.exists())
			{
				bw = new BufferedWriter(new FileWriter(_path));
			}else
			{
				bw = new BufferedWriter(new FileWriter(f,true));
			}
			
			
			for(String line : lines)
			{
				bw.append(line);
			}
			bw.close();
		} catch (Exception e) 
		{
			System.out.println("Coulndt write denizen folderscript");
		}
	}
	ArrayList<String> ReadDenizenTemplate()
	{
		ArrayList<String> lines = new ArrayList<>();
		try 
    	{
			//File f = new File(_path);
			BufferedReader br;
			
			InputStream input = getClass().getResourceAsStream("/DenizenScriptTempelate.txt");
    	    InputStreamReader inputReader = new InputStreamReader(input);
			br = new BufferedReader(inputReader);
			
    		//"DenizenScriptTempelate.txt"
    		
			String str;
			while((str = br.readLine()) != null )
			{
				lines.add(str+"\n");
			}
			br.close();
			
		} catch (Exception e) {
			System.out.println("Coulnt find DenizenScriptTemplate");
		}
		
		return lines;
	}
}
