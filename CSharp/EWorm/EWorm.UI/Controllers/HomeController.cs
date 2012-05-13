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

        public ActionResult Index()
        {
            return View();
        }
        [AcceptVerbs(HttpVerbs.Post)]
        public ActionResult Search(FormCollection formCollection)
        {
            string keyword = formCollection["keyword"];
            List<Goods> retViewList = new List<Goods>
                        {
                            new Goods(){Title="TEST ONE TEST ONE TEST ONE TEST ONE TEST ONE" + "[via:"+keyword+"]",PictrueUrl = "http://img01.taobaocdn.com/bao/uploaded/i1/T17uS6Xf0oXXahfOg0_035536.jpg_310x310.jpg", SellingUrl="http://item.taobao.com/item.htm?id=14287271419&ad_id=&am_id=&cm_id=&pm_id=1500479714fd39ddc4f8",Price=109 },
                            new Goods(){Title="TEST SECOND TEST SECOND TEST SECOND TEST SECOND" + "[via:"+keyword+"]",PictrueUrl = "http://img01.taobaocdn.com/bao/uploaded/i1/T17uS6Xf0oXXahfOg0_035536.jpg_310x310.jpg",SellingUrl="http://item.taobao.com/item.htm?id=14287271419&ad_id=&am_id=&cm_id=&pm_id=1500479714fd39ddc4f8",Price=109}
                        };

            return View(retViewList);
        }

    }
}
