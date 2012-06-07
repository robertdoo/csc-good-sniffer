using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Mvc;

namespace EWorm.UI.Controllers
{
    public class CrawlerController : Controller
    {
        public ActionResult Monitor()
        {
            return View();
        }


        public ActionResult KeywordQueue()
        {
            if (Crawler.Crawler.LastKeywordQueue == null)
            {
                return Json(new { has_result = false }, JsonRequestBehavior.AllowGet);
            }
            var arr = Crawler.Crawler.LastKeywordQueue.KeywordQueue.Take(50).Select(x => String.Format("{0} ({1})", x.Key, x.Value)).ToArray();
            var json = new
            {
                has_result = true,
                keyword_queue = arr
            };
            return Json(json, JsonRequestBehavior.AllowGet);
        }

        public ActionResult JobQueue()
        {
            if (Crawler.Crawler.LastJobQueue == null)
            {
                return Json(new { has_result = false }, JsonRequestBehavior.AllowGet);
            }
            var json = new
            {
                has_result = true,
                job_queue = Crawler.Crawler.LastJobQueue.JobQueue.Take(50).ToArray(),
                current_job = Crawler.Crawler.LastJobQueue.CurrentJob
            };
            return Json(json, JsonRequestBehavior.AllowGet);
        }
    }
}
