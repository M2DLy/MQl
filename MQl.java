/**
*	MQl project - 2017/7/30
*	Author  - Mohammad albai
*	Version - 0.4[Beta]
*	....
*	Discription : this project (Mini Query Language) provide you
*				  saving data in files and manage these data 
*				  (Query,Insert,Empty,Create,Delete) in easy way
*	<MQl extends="MQl_provider.class">
*		<version>0.4</version>
*		<methods>
*			<method name="CreateDataBase" />
*			<method name="CreateTable" />
*			<method name="Connect" />
*			<method name="TableExists" />
*			<method name="InsertData" />
*			<method name="Query" />
*			<method name="DataLength" />
*			<method name="__open_db__" />
*			<method name="__encode_data__" />
*			<method name="__decode_data__" />
*			<method name="__open__" />
*			<method name="__update_db_refrence__" />
*			<method name="__read_lines__" />
*			<method name="UseTable" />
*			<method name="GetDetials" />
*			<method name="EmptyTable" />
*			<method name="DeleteTable" />
*			<method name="EmptyDatabase" />
*			<method name="SetPasswors" />
*			<method name="CollectDataTable" />
*			<method name="Reset" />
*			<method name="__auto_increase_value__" />
*			<method name="Disconnect" />
*			<method name="__check_database_files__" />
*			<method name="__open_new" />
*		</methods>
*		<properties>
*			<property name="bw" />
*			<property name="tbw" />
*			<property name="DB_name" />
*			<property name="_error_" />
*			<property name="fc" />
*			<property name="_table_" />
*			<property name="MQlInsertFlag" />
*			<property name="MQlError" />
*			<property name="__pass__" />
*			<property name="__save_path__" />
*			<property name="__position_pointer__" />
*			<property name="ConnectError" />
*			<property name="MQlConnected" />
*			<property name="DataAsArray" />
*			<property name="tables" />
*			<property name="_table_row_names" />
*		</properties>
*	</MQl>
**/
/**
*	in the future (VERSION 0.5) :
*		SmartQuery : speed up fetching data by going to specific point to read from it
*		Query v1   : will speed query by detect what you looking for 
*		MQlParser {
*					AntiInject     : erase all 'inject' chars before insert 
*					CheckDataState : 2) steps , 1) depends on (__check_database_files__) 
*									 , then read database (depend on DB_name) , all files (.table) to find 'inject' chars 
*									 if this steps return false, then the content of injected file will save after remove 'inject' chars
*				    MaxDataLength  : after max length is reached , file(.table) will be closed and unable to write any more 
*				  }
*		Lock 	   : file(.table) will be unable to write any more
*		MQlRelation Class ... soon - (CREATE CONNECTION BETWEEN TO TABLES [1 by 1] TO LINK DATA)
*		BackupDatabase : provide you backup data when you loss it
*		UploadDatabase : probably it's a .Zip file which containing (.db) structer file and (.table) files 
*		HideErrors     : it prevent MQl class from reaching 'throw new Error()'
*		fixing (Index  : ? Lenth : ?) error
**/

import java.io.*;
import org.apache.commons.codec.binary.*;

public class MQl extends MQl_provider
{
	/*		CONFIGURATION PROPERTIES 	 */
	private String fc 					= "";
	private BufferedWriter tbw;
	private String[] _table_row_names 	= null;
	private String _table_ 				= null;
	public  boolean MQlInsertFlag 		= false;
	private String __pass__ 			= null;
	private int __position_pointer__ 	= -1;
	public  String[] DataAsArray 		= null;
	public  String[] tables 			= null;
    public  final String __save_path__  = "/storage/sdcard0/AppProjects/MyJavaConsoleApp/src/";
	public  boolean ConnectError 	    = false;
	public  String  MQlError 			= "";
	public  boolean MQlConnected 		= false;
	public  final String version 		= "0.4";
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
	private boolean TableExists(String name)
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
				if(new File(this.__save_path__+this.DB_name+"/"+name+".table").exists())
				{
					f = true; this.__position_pointer__ = i; break;
				}
				else { throw new Error("[MQl Table Exists] `"+name+"` table must have '/"+this.DB_name+"/"+name+".table' file"); }
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
		if(!this.TableExists(n) && !new File(this.__save_path__+this.DB_name+"/"+n+".table").exists()){throw new Error("[MQl UseTable Error] undefined `"+n+"` table in `"+this.DB_name+"`");}
		else{this._table_ = n;}
	}
	//
	// @param {}
	//
	public String GetDetials()
	{
		if(!this.MQlConnected) {this.ConnectError = true; this.MQlError = "[MQl Connection Error]  Connect object isn't defined yet "; throw new Error("[MQl connection error] connect object isn't defined  , access denided");}
		if(this._table_ == null || this.__position_pointer__ == -1) {throw new Error("[MQl Preparing Error] Connect object or table is't exists");}
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
		this.MQlInsertFlag = true;
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
	public void Query(String p,int lim) throws IOException
	{
		if(!this.MQlConnected) {this.ConnectError = true; this.MQlError = "[MQl Connection Error]  Connect object isn't defined yet "; throw new Error("[MQl connection error] connect object isn't defined  , access denided");}
		if(this.__check_database_files__()) { this.ConnectError = true; throw new Error(this.MQlError);}
		String[] l = this.__read_lines__(this._table_+".table").split("\n");
		String[] d = this.GetDetials().replaceAll(""+this._table_+" Detials. ","").split(",");
		int i = 0;
		boolean c = false;
		for(;i<d.length;i++)
		{
			if(d[i].trim().equals(p)){
			c = true;	break;
			}
		}
		if(!c) { this.MQlError = "[MQl Query Error] `"+p+"` is not a column name in `"+this.DB_name+"`";throw new Error(this.MQlError); }
		this.DataAsArray = new String[l.length];
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
	public String[][] CollectDataTable(String[] data,int l) throws IOException
	{
		String[][] d = new String[data.length][];
			for(int i=0;i<d.length;i++)
			{
				this.Query(data[i],l);
				d[i] = this.DataAsArray;
			}
			String[][] md = new String[data.length][];
			if(l == 2) {md = d;}
			else if(l == 0)
			{
				for(int i = 0; i<d.length;i++)
				{
					md[i][0] = d[i][0];
				}
			}
		this.DataAsArray =  null;
		d = null;
		return md;
	}
	//
	//	@param $p(data to be update)
	//
	public void Update(String p){}
	//
	//	@param $p(data to be delete)
	//
	public void Delete(String p){}
	//
	//	@param $password
	//
	public void SetPassword(String p)
	{
		
	}
	//
	//	@param {}
	//
	private int DataLength() throws IOException
	{
		String[] d = this.__read_lines__(this._table_+".table").split("\n");
		return d.length;
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
	// @param {} @return boolean
	//
	private boolean __check_database_files__()
	{
		if(this.tables == null) { return false;}
		boolean c = false;
		File f;
		for(int i = 0;i<this.tables.length;i++)
		{
			f =  new File(this.__save_path__+this.DB_name+"/"+this.tables[i]+".table");
			if(!f.exists()) {c = false; this.MQlError = "[MQl Seurity Checker] file '/"+this.DB_name+"/"+this.tables[i]+".table' isn't exists"; break;}
		}
		return c;
	}
	
	//
	//	@param {}
	//
	public void Disconnect() throws IOException {if(!this.MQlConnected) {this.ConnectError = true; this.MQlError = "[MQl Connection Error]  Connect object isn't defined yet "; throw new Error("[MQl connection error] connect object isn't defined  , access denided");}	this.fc = ""; this.bw = null; this.tbw = null; this.DB_name = ""; this._table_ = null; this.__position_pointer__ = -1;	}
} 
