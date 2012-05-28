using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace EWorm.Model
{
    public class StringProperty : Property
    {
        public String Value { get; set; }

        public static StringProperty BuildFromString(string name, string value)
        {
            return new StringProperty() { Name = name, Type = PropertyType.String, Value = value };
        }
    }
}
