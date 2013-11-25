using System;
using System.Collections.Generic;
using System.Data;
using System.Data.SQLite;
using System.Windows.Forms;
using System.IO;

class SQLiteDatabase
{
    String dbConnection;

    /// <summary>
    ///     Default Constructor for SQLiteDatabase Class.
    /// </summary>
    //public SQLiteDatabase()
    //{
    //    dbConnection = "Data Source=recipes.s3db";
    //}

    /// <summary>
    ///     Single Param Constructor for specifying the DB file.
    /// </summary>
    /// <param name="inputFile">The File containing the DB</param>
    //public SQLiteDatabase(String inputFile)
    //{
    //    dbConnection = String.Format("Data Source={0}", inputFile);
    //}

    /// <summary>
    ///     Single Param Constructor for specifying advanced connection options.
    /// </summary>
    /// <param name="connectionOpts">A dictionary containing all desired options and their values</param>
    //public SQLiteDatabase(Dictionary<String, String> connectionOpts)
    //{
    //    String str = "";
    //    foreach (KeyValuePair<String, String> row in connectionOpts)
    //    {
    //        str += String.Format("{0}={1}; ", row.Key, row.Value);
    //    }
    //    str = str.Trim().Substring(0, str.Length - 1);
    //    dbConnection = str;
    //}

    public SQLiteDatabase(FileInfo databaseFile)
    {
        dbConnection = GetDataSource(databaseFile);
    }

    public static string GetDataSource(FileInfo databaseFile)
    {
        return "Data Source=" + databaseFile.FullName + ";Compress=True;";
    }

    public static SQLiteConnection GetConnection(FileInfo databaseFile)
    {
        return new SQLiteConnection(GetDataSource(databaseFile));
    }

    /// <summary>
    /// Allows the programmer to run a query against the Database.
    /// </summary>
    /// <param name="sql">The SQL to run</param>
    /// <returns>A DataTable containing the result set.</returns>
    public DataTable GetDataTable(string sql)
    {
        DataTable dt = new DataTable();
        try
        {
            SQLiteConnection cnn = new SQLiteConnection(dbConnection);
            cnn.Open();
            SQLiteCommand mycommand = new SQLiteCommand(cnn);
            mycommand.CommandText = sql;
            SQLiteDataReader reader = mycommand.ExecuteReader();
            dt.Load(reader);
            reader.Close();
            cnn.Close();
        }
        catch (Exception e)
        {
            throw new Exception(e.Message);
        }
        return dt;
    }

    /// <summary>
    ///     Allows the programmer to interact with the database for purposes other than a query.
    /// </summary>
    /// <param name="sql">The SQL to be run.</param>
    /// <returns>An Integer containing the number of rows updated.</returns>
    public int ExecuteNonQuery(string sql)
    {
        Console.WriteLine("SQL: " + sql);
        SQLiteConnection cnn = new SQLiteConnection(dbConnection);
        cnn.Open();
        SQLiteCommand mycommand = new SQLiteCommand(cnn);
        mycommand.CommandText = sql;
        int rowsUpdated = mycommand.ExecuteNonQuery();

        cnn.Close();
        return rowsUpdated;
    }

    public int ExecuteInsert(string sql)
    {
        Console.WriteLine("SQL: " + sql);
        SQLiteConnection cnn = new SQLiteConnection(dbConnection);
        cnn.Open();
        SQLiteCommand mycommand = new SQLiteCommand(cnn);
        mycommand.CommandText = sql;
        int rowsUpdated = mycommand.ExecuteNonQuery();

        mycommand.CommandText = "SELECT last_insert_rowid();";
        int newIndex = int.Parse(mycommand.ExecuteScalar().ToString());

        cnn.Close();
        return newIndex;
    }

    /// <summary>
    ///     Allows the programmer to retrieve single items from the DB.
    /// </summary>
    /// <param name="sql">The query to run.</param>
    /// <returns>A string.</returns>
    public string ExecuteScalar(string sql)
    {
        Console.WriteLine("SQL: " + sql);
        SQLiteConnection cnn = new SQLiteConnection(dbConnection);
        cnn.Open();
        SQLiteCommand mycommand = new SQLiteCommand(cnn);
        mycommand.CommandText = sql;
        object value = mycommand.ExecuteScalar();
        cnn.Close();
        if (value != null)
        {
            return value.ToString();
        }
        return "";
    }

    public string Max(string tableName, string column)
    {
        return this.ExecuteScalar("SELECT MAX("+column+") FROM " + tableName);
    }

    /// <summary>
    ///     Allows the programmer to easily update rows in the DB.
    /// </summary>
    /// <param name="tableName">The table to update.</param>
    /// <param name="data">A dictionary containing Column names and their new values.</param>
    /// <param name="where">The where clause for the update statement.</param>
    /// <returns>A boolean true or false to signify success or failure.</returns>
    public bool Update(String tableName, Dictionary<String, String> data, String where)
    {
        String vals = "";
        Boolean returnCode = true;
        if (data.Count >= 1)
        {
            foreach (KeyValuePair<String, String> val in data)
            {
                vals += String.Format(" {0} = '{1}',", val.Key.ToString(), val.Value.ToString());
            }
            vals = vals.Substring(0, vals.Length - 1);
        }
        try
        {
            this.ExecuteNonQuery(String.Format("update {0} set {1} where {2};", tableName, vals, where));
        }
        catch
        {
            returnCode = false;
        }
        return returnCode;
    }

    /// <summary>
    ///     Allows the programmer to easily delete rows from the DB.
    /// </summary>
    /// <param name="tableName">The table from which to delete.</param>
    /// <param name="where">The where clause for the delete.</param>
    /// <returns>A boolean true or false to signify success or failure.</returns>
    public bool Delete(String tableName, String where)
    {
        Boolean returnCode = true;
        String sql = null;
        try
        {
            sql = String.Format("delete from {0} where {1};", tableName, where);
            this.ExecuteNonQuery(sql);
        }
        catch (Exception fail)
        {
            MessageBox.Show("Delete: " + sql + " :: " + fail.Message);
            returnCode = false;
        }
        return returnCode;
    }

    /// <summary>
    ///     Allows the programmer to easily insert into the DB
    /// </summary>
    /// <param name="tableName">The table into which we insert the data.</param>
    /// <param name="data">A dictionary containing the column names and data for the insert.</param>
    /// <returns>A number representing the index this item is filling</returns>
    public int Insert(String tableName, Dictionary<String, String> data)
    {
        String columns = "";
        String values = "";

        foreach (KeyValuePair<String, String> val in data)
        {
            columns += String.Format(" {0},", val.Key.ToString());
            values += String.Format(" '{0}',", val.Value);
        }

        if (columns.Length > 0)
            columns = columns.Substring(0, columns.Length - 1);
        if (values.Length > 0)
            values = values.Substring(0, values.Length - 1);

        string sql = String.Format("INSERT INTO {0} ({1}) VALUES ({2});", tableName, columns, values);
        
        int pos = 0;

        try
        {
            pos = this.ExecuteInsert(sql);
        }
        catch (Exception fail)
        {
            MessageBox.Show("Insert: " + sql + " :: " + fail.Message);
        }
        return pos;
    }

    /// <summary>
    /// Allows the programmer to quickly insert multiple items into a database
    /// </summary>
    /// <param name="tableName"></param>
    /// <param name="data"></param>
    /// <returns></returns>
    public int Insert(String tableName, IList<Dictionary<String, String>> allData)
    {
        int modified = 0;

        using (SQLiteConnection cnn = new SQLiteConnection(dbConnection))
        {
            cnn.Open();
            using (SQLiteTransaction dbTrans = cnn.BeginTransaction())
            {
                // start the loop

                foreach (Dictionary<String, String> data in allData)
                {
                    String columns = "";
                    String values = "";
                    foreach (KeyValuePair<String, String> val in data)
                    {
                        columns += String.Format(" {0},", val.Key.ToString());
                        values += String.Format(" ?,");
                    }
                    columns = columns.Substring(0, columns.Length - 1);
                    values = values.Substring(0, values.Length - 1);
                    try
                    {
                        string sql = String.Format("insert into {0}({1}) values({2});", tableName, columns, values);
                        SQLiteCommand dbCommand = new SQLiteCommand(cnn);


                        foreach (KeyValuePair<String, String> val in data)
                        {
                            SQLiteParameter para = dbCommand.CreateParameter();
                            para.Value = val.Value;
                            dbCommand.Parameters.Add(para);
                        }

                        dbCommand.CommandText = sql;
                        int rowsUpdated = dbCommand.ExecuteNonQuery();
                        modified += rowsUpdated;
                    }
                    catch (Exception fail)
                    {
                        MessageBox.Show(fail.Message);
                    }
                }
                dbTrans.Commit();
            }
        }
        return modified;
    }

    public static int Insert(SQLiteConnection connection, String tableName, Dictionary<String, String> data)
    {
        String columns = "";
        String values = "";
        foreach (KeyValuePair<String, String> val in data)
        {
            columns += String.Format(" {0},", val.Key.ToString());
            values += String.Format(" ?,");
        }
        columns = columns.Substring(0, columns.Length - 1);
        values = values.Substring(0, values.Length - 1);
        try
        {
            string sql = String.Format("insert into {0}({1}) values({2});", tableName, columns, values);
            SQLiteCommand dbCommand = new SQLiteCommand(connection);


            foreach (KeyValuePair<String, String> val in data)
            {
                SQLiteParameter para = dbCommand.CreateParameter();
                para.Value = val.Value;
                dbCommand.Parameters.Add(para);
            }


            dbCommand.CommandText = sql;
            int rowsUpdated = dbCommand.ExecuteNonQuery();

            dbCommand.CommandText = "SELECT last_insert_rowid();";
            return int.Parse(dbCommand.ExecuteScalar().ToString()); // returned index
        }
        catch (Exception fail)
        {
            MessageBox.Show(fail.Message);
        }
        return 0;
    }

    /// <summary>
    ///     Allows the programmer to easily delete all data from the DB.
    /// </summary>
    /// <returns>A boolean true or false to signify success or failure.</returns>
    public bool ClearDB()
    {
        DataTable tables;
        try
        {
            tables = this.GetDataTable("select NAME from SQLITE_MASTER where type='table' order by NAME;");
            foreach (DataRow table in tables.Rows)
            {
                string table_name = table["NAME"].ToString();
                if (table_name.StartsWith("sqlite"))
                    continue;
                if (table_name.StartsWith("android"))
                    continue;

                this.ClearTable(table_name);
            }
            return true;
        }
        catch
        {
            return false;
        }
    }

    /// <summary>
    ///     Allows the user to easily clear all data from a specific table.
    /// </summary>
    /// <param name="table">The name of the table to clear.</param>
    /// <returns>A boolean true or false to signify success or failure.</returns>
    public bool ClearTable(String table)
    {
        try
        {
            this.ExecuteNonQuery(String.Format("delete from {0};", table));
            return true;
        }
        catch
        {
            return false;
        }
    }

    public void ClearTable(IList<string> tables)
    {
        foreach (string table in tables)
        {
            this.ClearTable(table);
        }
    }
}

