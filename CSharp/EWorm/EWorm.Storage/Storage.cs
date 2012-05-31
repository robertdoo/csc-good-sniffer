using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using EWorm.Model;
using MySql.Data.MySqlClient;

namespace EWorm.Storage
{
    public class Storage : IDisposable
    {
        private String ConnectionString { get; set; }
        private MySqlConnection Connection { get; set; }

        public Storage(String connectionString)
        {
            this.ConnectionString = connectionString;
            this.Connection = new MySqlConnection(this.ConnectionString);
            this.Connection.Open();
        }

        public void SaveGoods(Goods goods)
        {
            if (goods.Id > 0)
                UpdateGoods(goods);
            else
                InsertGoods(goods);
        }

        private void InsertGoods(Goods goods)
        {
            MySqlCommand cmd = this.Connection.CreateCommand();
            cmd.CommandText = @"INSERT INTO t_goods
                                (title, sellingurl, updatetime, imagepath, price, sellercredit, sellamount)
                                VALUES
                                (?title, ?sellingurl, ?updatetime, ?imagepath, ?price, ?sellercredit, ?sellamount)";
            cmd.Parameters.AddWithValue("title", goods.Title);
            cmd.Parameters.AddWithValue("sellingurl", goods.SellingUrl);
            cmd.Parameters.AddWithValue("updatetime", DateTime.Now);
            cmd.Parameters.AddWithValue("imagepath", goods.ImagePath);
            cmd.Parameters.AddWithValue("price", goods.Price);
            cmd.Parameters.AddWithValue("sellercredit", goods.SellerCredit);
            cmd.Parameters.AddWithValue("sellamount", goods.SellAmount);
            cmd.ExecuteNonQuery();
            goods.Id = (int)cmd.LastInsertedId;
            UpdateProperties(goods);
        }

        private void UpdateGoods(Goods goods)
        {
            MySqlCommand cmd = this.Connection.CreateCommand();
            cmd.CommandText = @"UPDATE t_goods
                                SET title = ?title, sellingurl = ?sellingurl, updatetime = ?updatetime, imagepath = ?imagepath, price = ?price, sellercredit = ?sellercredit, sellamount = ?sellamount
                                WHERE id = ?id";
            cmd.Parameters.AddWithValue("id", goods.Id);
            cmd.Parameters.AddWithValue("title", goods.Title);
            cmd.Parameters.AddWithValue("sellingurl", goods.SellingUrl);
            cmd.Parameters.AddWithValue("updatetime", goods.UpdateTime);
            cmd.Parameters.AddWithValue("imagepath", goods.ImagePath);
            cmd.Parameters.AddWithValue("price", goods.Price);
            cmd.Parameters.AddWithValue("sellercredit", goods.SellerCredit);
            cmd.Parameters.AddWithValue("sellamount", goods.SellAmount);
            cmd.ExecuteNonQuery();
            UpdateProperties(goods);
        }


        private void UpdateProperties(Goods goods)
        {            
            MySqlCommand clearCmd = this.Connection.CreateCommand();
            clearCmd.CommandText = @"DELETE FROM t_properties
                                     WHERE goodsid = ?goodsid";
            clearCmd.Parameters.AddWithValue("goodsid", goods.Id);
            clearCmd.ExecuteNonQuery();
            if (goods.Properties == null)
                return;
            foreach (var property in goods.Properties)
            {
                MySqlCommand insertCmd = this.Connection.CreateCommand();
                insertCmd.CommandText = @"INSERT into t_properties
                                          (goodsid, propertyname, propertyvalue, propertytype)
                                          VALUES
                                          (?goodsid, ?propertyname, ?propertyvalue, ?propertytype)";
                insertCmd.Parameters.AddWithValue("goodsid", goods.Id);
                insertCmd.Parameters.AddWithValue("propertyname", property.Name);
                insertCmd.Parameters.AddWithValue("propertytype", property.Type.ToString());
                if (property is StringProperty)
                {
                    insertCmd.Parameters.AddWithValue("propertyvalue", (property as StringProperty).Value);
                }
                else if (property is IntegerProperty)
                {
                    insertCmd.Parameters.AddWithValue("propertyvalue", (property as IntegerProperty).Value.ToString());
                }
                insertCmd.ExecuteNonQuery();
            }
        }

        public IEnumerable<Goods> SearchGoods(string keyword, int start, int limit, string order, bool desc)
        {
            List<Goods> result = new List<Goods>();
            MySqlCommand cmd = this.Connection.CreateCommand();
            cmd.CommandText = @"SELECT id, title, sellingurl, updatetime, imagepath, price, sellercredit, sellamount
                                FROM t_goods
                                WHERE title LIKE CONCAT('%', ?keyword ,'%')";
            if (order != null)
            {
                cmd.CommandText += " ORDER BY " + order;
                if (desc)
                {
                    cmd.CommandText += " DESC";
                }
            }
            cmd.CommandText += String.Format(" LIMIT {0},{1}", start, limit);
            cmd.Parameters.AddWithValue("keyword", keyword);
            MySqlDataReader reader = cmd.ExecuteReader();
            while (reader.Read())
            {
                Goods goods = ReadGoods(reader);
                result.Add(goods);
            }
            reader.Close();
            foreach (var goods in result)
            {
                FillProperties(goods);
            }
            return result;
        }

        public Goods GetGoodsById(int id)
        {
            MySqlCommand cmd = this.Connection.CreateCommand();
            cmd.CommandText = @"SELECT id, title, sellingurl, updatetime, imagepath, price, sellercredit, sellamount
                                FROM t_goods
                                WHERE id=?id";
            cmd.Parameters.AddWithValue("id", id);
            MySqlDataReader reader = cmd.ExecuteReader();
            if (reader.Read())
            {
                Goods goods = ReadGoods(reader);
                reader.Close();
                FillProperties(goods);
                return goods;
            }
            reader.Close();
            return null;
        }

        public Goods GetGoodsBySellingUrl(string url)
        {
            MySqlCommand cmd = this.Connection.CreateCommand();
            cmd.CommandText = @"SELECT id, title, sellingurl, updatetime, imagepath, price, sellercredit, sellamount
                                FROM t_goods
                                WHERE sellingurl=?sellingurl";
            cmd.Parameters.AddWithValue("sellingurl", url);
            MySqlDataReader reader = cmd.ExecuteReader();
            if (reader.Read())
            {
                Goods goods = ReadGoods(reader);
                reader.Close();
                FillProperties(goods);
                return goods;
            }
            reader.Close();
            return null;
        }

        private static Goods ReadGoods(MySqlDataReader reader)
        {
            Goods goods = new Goods()
            {
                Id = reader.GetInt32("id"),
                Title = reader.GetString("title"),
                ImagePath = reader.GetString("imagepath"),
                Price = reader.GetInt32("price"),
                SellAmount = reader.GetInt32("sellamount"),
                SellerCredit = reader.GetInt32("sellercredit"),
                SellingUrl = reader.GetString("sellingurl"),
                UpdateTime = reader.GetDateTime("updatetime")
            };
            return goods;
        }

        private void FillProperties(Goods goods)
        {
            MySqlCommand cmd = this.Connection.CreateCommand();
            cmd.CommandText = @"SELECT propertyname, propertyvalue, propertytype
                                FROM t_properties
                                WHERE goodsid = ?goodsid";
            cmd.Parameters.AddWithValue("goodsid", goods.Id);
            MySqlDataReader reader = cmd.ExecuteReader();
            List<Property> propertyList = new List<Property>();
            while (reader.Read())
            {
                string name, value, type;
                name = reader.GetString("propertyname");
                value = reader.GetString("propertyvalue");
                type = reader.GetString("propertytype");
                PropertyType pType = (PropertyType)Enum.Parse(typeof(PropertyType), type);
                switch (pType)
                {
                    case PropertyType.String:
                        propertyList.Add(StringProperty.BuildFromString(name, value));
                        break;
                    case PropertyType.Integer:
                        propertyList.Add(StringProperty.BuildFromString(name, value));
                        break;
                    default:
                        break;
                }
            }
            reader.Close();
            goods.Properties = propertyList;
        }

        public void Dispose()
        {
            this.Connection.Close();
        }
    }
}
