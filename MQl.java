/**
*	MQl project - 2017/7/30
*	Author  - Mohammad albai
*	Version - 0.3[Beta]
*	THIS PROJECT IS FREE TO USE BUT IT'S NOT OPEN SOURCE YET!
*	Discription : this project (Mini Query Language) provide you from saving data in files and manage thats data 
*				  (Query,Insert,Empty,Create,Delete) , MQl class had all methods to make everything easy 
**/


import java.io.*;
import org.apache.commons.codec.binary.*;

public class MQl extends MQl_provider
{
	/*		CONFIGURATION PROPERTIES 	 */
	private String fc = "";
	private BufferedWriter tbw;
	private String[] _table_row_names = null;
	private String _table_ = null;
	private String __pass__ = null;
	private int __position_pointer__ = -1;
	public String[] DataAsArray = null;
	public String[] tables = null;
    public final String __save_path__ = "/storage/sdcard0/AppProjects/MyJavaConsoleApp/src/";
	public boolean ConnectError = false;
	public String  MQlError = "";
	public boolean MQlConnected = false;
	//
	//	@param $name,$password
	//
	public void CreateDataBase(String n,String p) throws IOException
	{
		if(!this.__open__(n,p)){this.ConnectError = true; this.MQlError = this._error_;}
		
	}
	//
	//	@param $name,$password
	//
	public void Connect(String db_name,String pass) throws FileNotFoundException, IOException
	{
		this.__open__(db_name,pass);
		this.tables = new String[50];
		FileReader f = new FileReader("/storage/sdcard0/AppProjects/MyJavaConsoleApp/src/"+db_name+"/"+db_name+".db");
		BufferedReader br = new BufferedReader(f);
		String l = "",r = "";
		while((l = br.readLine()) != null)
		{
			r += l+"\n";
		}this.fc = r;
		String z = r.split("\n")[0].split(":")[1];
		if(!z.equals(new String(Base64.encodeBase64(pass.getBytes())))) { this.ConnectError = true; this.MQlError = "[MQl Connection Error]  invalid passwors ,Access denided "; throw new Error("[MQl connection error] invalid password , access denided");} 
		else{this.__pass__ = pass;	this.MQlConnected = true;}
	}
	//
	//	@param $data[table-name,(table column...n)]
	//
	public void CreateTable(String[] data) throws IOException
	{
		if(!this.MQlConnected) {this.ConnectError = true; this.MQlError = "[MQl Connection Error]  Connect object isn't defined yet "; throw new Error("[MQl connection error] connect object isn't defined  , access denided");}
		if(this.TableExists(data[0])){throw new Error("[MQl CreateTable Error] table is already exist");}
		System.out.println(data[0]+";"+this.fc);
		int l = data.length;
		if(l == 0){throw new Error("[MQl CreateTable Error] can't create table without detials");}
		this.bw.write(data[0]+"/");
		this.bw.write((l-1)+",");
		this.__open_new(data[0]);
		for(int i = 1;i<l;i++)
		{
			this.bw.write(data[i]+",");
		}
		this.bw.write("\n");
		this.bw.flush();
		this.fc = this.__update_db_refrence__();
	}
	//
	//	@param $table-name @return boolean
	//
	public boolean TableExists(String name)
	{
		if(!this.MQlConnected) {this.ConnectError = true; this.MQlError = "[MQl Connection Error]  Connect object isn't defined yet "; throw new Error("[MQl connection error] connect object isn't defined  , access denided");}
		boolean f = false;
		String[] t = this.fc.split("\n");
		int i = 1;
		for(;i<t.length;i++)
		{
			String ta = t[i].split("/")[0];
			this.tables[i] = t[i];
			if(ta.equals(name.trim())){
				f = true; this.__position_pointer__ = i; break;
			}
		}
		return f;
	}
	//
	//	@param $table-name
	//
	public void UseTable(String n)
	{
		if(!this.MQlConnected) {this.ConnectError = true; this.MQlError = "[MQl Connection Error]  Connect object isn't defined yet "; throw new Error("[MQl connection error] connect object isn't defined  , access denided");}
		if(!this.TableExists(n)){throw new Error("[MQl UseTable Error] undefined `"+n+"` table in `"+this.DB_name+"`");}
		else{this._table_ = n;}
	}
	//
	// @param {}
	//
	public String GetDetials()
	{
		if(!this.MQlConnected) {this.ConnectError = true; this.MQlError = "[MQl Connection Error]  Connect object isn't defined yet "; throw new Error("[MQl connection error] connect object isn't defined  , access denided");}
		if(this._table_ == null || this.__position_pointer__ == -1) {throw new Error("[MQl Update DB Refrence Error] UseTable Error");}
		String[] c = this.fc.split("\n");
		String r = ""+this._table_+" Detials. ";
		for(int i = 1;i<c.length;i++)
		{
			String epx = c[i].split("/")[1];
			if(i == this.__position_pointer__)
			{
				String[] ex = epx.split(",");
				for(i = 1;i<ex.length;i++)
				{
					r += ex[i]+",";
				}
				break;
			}
		}
		return r;
	}
	//
	//	@param $data{(data-row...n)}
	//
	public void InsertData(String[] data) throws IOException
	{
		if(!this.MQlConnected) {this.ConnectError = true; this.MQlError = "[MQl Connection Error]  Connect object isn't defined yet "; throw new Error("[MQl connection error] connect object isn't defined  , access denided");}
		if(this._table_ == null || this.__position_pointer__ == -1) {throw new Error("[MQl Insert Data Error] ");}
		String res = "";
		int v = -1;
		this.tbw = new BufferedWriter(new FileWriter("/storage/sdcard0/AppProjects/MyJavaConsoleApp/src/"+this.DB_name+"/"+this._table_+".table",true));
		String[] c = this.fc.split("\n");
		for(int i = 1;i<c.length;i++)
		{
			String[] ex = c[i].split("/");
			v = Integer.parseInt( ex[1].split(",")[0] );
			if(i == this.__position_pointer__)	{	break;	}
		}
		if(data.length > v) {throw new Error("[MQl InsertData Error] data length is out load");}
		for(int i = 0;i<data.length;i++)
		{
			this.tbw.write(this._encode_data_(data[i])+",");
		}
		this.tbw.write("\n");
		this.tbw.flush();
	}
	//
	//	@param {}
	//
	public void EmptyTable() throws IOException
	{
		if(!this.MQlConnected) {this.ConnectError = true; this.MQlError = "[MQl Connection Error]  Connect object isn't defined yet "; throw new Error("[MQl connection error] connect object isn't defined  , access denided");}
		new File(this.__save_path__+this.DB_name+"/"+this._table_+".table").delete();
		this.__open_new(this._table_);
	}
	//
	// @param $query-string(get data as array)
	//
	public void Query(String p) throws IOException
	{
		if(!this.MQlConnected) {this.ConnectError = true; this.MQlError = "[MQl Connection Error]  Connect object isn't defined yet "; throw new Error("[MQl connection error] connect object isn't defined  , access denided");}
		String[] l = this.__read_lines__(this._table_+".table").split("\n");
		String[] d = this.GetDetials().replaceAll(""+this._table_+" Detials. ","").split(",");
		int i = 0;
		for(;i<d.length;i++)
		{
			if(d[i].trim().equals(p)){
				break;
			}
		} this.DataAsArray = new String[l.length];
		for(int j = 1;j<l.length;j++)
		{
			String row = l[j].split(",")[i];
			this.DataAsArray[j-1] = this._decode_data_(row);
		}
	}
	//
	//	@param $table-name
	//
	public void DeleteTable(String p) throws IOException
	{
		if(!this.MQlConnected || this.__position_pointer__ != -1) {this.ConnectError = true; this.MQlError = "[MQl Connection Error]  Connect object isn't defined yet "; throw new Error("[MQl connection error] connect object isn't defined  , access denided");}
		String[] d = this.__update_db_refrence__().split("\n");
		String cont = "";
		File f = new File(this.__save_path__+this.DB_name+"/"+this.DB_name+".db");
		for(int j = 1;j<d.length;j++)
		{
			if(j == this.__position_pointer__) {}
			else
			{
				cont += d[j];
			}
		}
		f.delete();
		this.CreateDataBase(this.DB_name,this.__pass__);
		new File(this.__save_path__+this.DB_name+"/"+p+".table").delete();
	}
	//
	// @param $data-collection
	//
	public void CollectDataTable()
	{
		
	}
	//
	//	@param $password
	//
	public void SetPassword(String p)
	{
		
	}
	//
	//	@param {}
	//
	public void DataLength()
	{
		
	}
	//
	//	@param {}
	//
	public void EmptyDatabase() throws IOException
	{
		File f = new File(this.__save_path__+this.DB_name+"/"+this.DB_name+".db");
		f.delete();
		this.CreateDataBase(this.DB_name,this.__pass__);
	}
	//
	//	@param {}
	//
	public void Reset()
	{
		if(!this.MQlConnected) {this.ConnectError = true; this.MQlError = "[MQl Connection Error]  Connect object isn't defined yet "; throw new Error("[MQl connection error] connect object isn't defined  , access denided");}
		this.DataAsArray = null;
		this.__position_pointer__ = -1;
		this._table_row_names = null;
		this._table_ = null;
		this.DB_name = null;
		this.fc = null;
		this.MQlConnected = false;
		this.tables = null;
	}
	//
	//	@param $p @return int
	//
	private int __auto_increase_value__(String p) throws IOException
	{
		try
		{
		String[] val = this.__read_lines__(this.__save_path__+this.DB_name+"/"+this._table_+".table").split("\n");
		String v = val[val.length-1]; /* GET LAST ELEMENT */
		String exp = v.split(",")[0];
		int max_val = Integer.parseInt(exp);
		return max_val;
		}
		catch(Exception e)
		{
			throw new Error("[QMl Auto Increase] failed to parse value");
		}
	}
	//
	//	@param {}
	//
	public void Disconnect() throws IOException {if(!this.MQlConnected) {this.ConnectError = true; this.MQlError = "[MQl Connection Error]  Connect object isn't defined yet "; throw new Error("[MQl connection error] connect object isn't defined  , access denided");}	this.fc = ""; this.bw = null; this.tbw = null; this.DB_name = ""; this._table_ = null; this.__position_pointer__ = -1;	}
} 
