using GovUk.Frontend.AspNetCore.Extensions.Validation;
using MermaidWorkflow.CMS.Models;
using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Mvc.ViewEngines;
using Umbraco.Cms.Core.Web;
using Umbraco.Cms.Web.Common.Controllers;
using Umbraco.Cms.Web.Common.PublishedModels;

namespace MermaidWorkflow.CMS.Controllers;

public class PlaygroundController : RenderController
{
    public PlaygroundController(ILogger<RenderController> logger, ICompositeViewEngine compositeViewEngine, IUmbracoContextAccessor umbracoContextAccessor) : base(logger, compositeViewEngine, umbracoContextAccessor)
    {
    }

    [ModelType(typeof(PlaygroundViewModel))]
    public override IActionResult Index()
    {
        var viewModel = new PlaygroundViewModel
        {
            Page = new Playground(CurrentPage, null)
        };
        var ret =  CurrentTemplate(viewModel);
        return ret;
    }
}
