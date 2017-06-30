import java.io.*;
import org.apache.commons.codec.binary.*;

public class MQl extends MQl_provider
{
	private String __save_path__ = "/storage/sdcard0/AppProjects/MyJavaConsoleApp/src/";
	private String fc = "";
	private String[] tables = null;
	private String[] _table_row_names = null;
	public String _table_ = null;
	
	public String[] DataAsArray = null;
	private int __position_pointer__ = -1;
	private BufferedWriter tbw;
	public boolean ConnectError = false;
	public String  MQlError = "";
	public boolean MQlConnected = false;
	public void CreateDataBase(String n,String p) throws IOException
	{
		if(!this.__open__(n,p)){this.ConnectError = true; this.MQlError = this._error_;}
		
	}
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
		else{this.MQlConnected = true;}
	}
	public void CreateTable(String[] data) throws IOException
	{
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
	public boolean TableExists(String name)
	{
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
	public void UseTable(String n)
	{
		if(!this.TableExists(n)){throw new Error("[MQl UseTable Error] undefined `"+n+"` table in `"+this.DB_name+"`");}
		else{this._table_ = n;}
	}
	public String GetDetials()
	{
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
	public void InsertData(String[] data) throws IOException
	{
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
	public void EmptyTable() throws IOException
	{
		new File(this.__save_path__+this.DB_name+"/"+this._table_+".table").delete();
		this.__open_new(this._table_);
	}
	public void Query(String p) throws IOException
	{
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
			this.DataAsArray[j] = this._decode_data_(row);
		}
	}
	public void Reset()
	{
		this.DataAsArray = null;
		this.__position_pointer__ = -1;
		this._table_row_names = null;
		this._table_ = null;
		this.DB_name = null;
		this.fc = null;
		this.MQlConnected = false;
		this.tables = null;
	}
	public void Disconnect() throws IOException {	this.fc = ""; this.bw = null; this.tbw = null; this.DB_name = ""; this._table_ = null; this.__position_pointer__ = -1;	}
} 
