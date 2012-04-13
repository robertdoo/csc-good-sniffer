using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace EWorm.Model
{
    public abstract class Property
    {
        public String Name { get; set; }
        public PropertyType Type { get; set; }
    }
}
