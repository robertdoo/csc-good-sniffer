using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Mvc;
using EWorm.Model;
namespace EWorm.UI.Controllers
{
    public class HomeController : Controller
    {
        //
        // GET: /Home/
        IEnumerable<Goods> result;
        public ActionResult Index()
        {
            return View();
        }
        [AcceptVerbs(HttpVerbs.Post)]
        public ActionResult Search(FormCollection formCollection)
        {
            string q = formCollection["keyword"];
            string s = Request["s"];
            string o = Request["o"];
            string d = Request["d"];
            if (q == null)
            {
                return View();
            }
            else
            {
                int start = Convert.ToInt32(s);
                Service service = new Service();
                result = service.Search(q, start, 50, o, d == "true");
                return View("Search", result);
            }
        }
        public ActionResult detail(long id)
        {

            Goods goods = new Goods(){Title="TEST ONE TEST ONE TEST ONE TEST ONE TEST ONE"+ "[via:"+id+"]"};

            return View(goods);
        }
    }
}
