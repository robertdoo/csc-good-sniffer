using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace EWorm.Model
{
    public class IntegerProperty : Property
    {
        public int Value { get; set; }

        public static IntegerProperty BuildFromString(string name, string value)
        {
            return new IntegerProperty() { Name = name, Type = PropertyType.Integer, Value = Convert.ToInt32(value) };
        }
        public override String ValueAsString
        {

            get {return Value.ToString();}
        }
    }
}
