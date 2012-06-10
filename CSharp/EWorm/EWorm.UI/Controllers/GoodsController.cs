using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Mvc;
using EWorm.Model;
using Techird.Mvc3Tools;

namespace EWorm.UI.Controllers
{
    public class GoodsController : Controller
    {
        public ActionResult Index()
        {
            return View();
        }

        public ActionResult Search()
        {
            string query = Request["q"]; // 查询关键字
            string page = Request["p"]; // 页码
            string order = Request["o"]; // 排序
            string desc = Request["d"]; // 是否降序
            if (String.IsNullOrEmpty(query))
            {
                return View(null as object);
            }
            else
            {
                Service service = new Service();
                Pager pager = new Pager()
                {
                    CurrentPage = Convert.ToInt32(page),
                    RecordCount = service.SearchCount(query),
                    FirstText = "首页",
                    LastText = "尾页",
                    NextText = "下一页",
                    PrevText = "上一页",
                    PageSize = 60
                };
                if (pager.CurrentPage == 0)
                    pager.CurrentPage = 1;
                int start = pager.PageSize * (pager.CurrentPage - 1),
                    limit = pager.PageSize;
                var result = service.Search(query, start, limit, order, desc == "true").BuildPagedResult(pager);
                return View(result);
            }
        }

        public ActionResult Detail(int id)
        {
            Service service = new Service();
            Goods goods = service.GetGoods(id);

            return View(goods);
        }
    }
}
