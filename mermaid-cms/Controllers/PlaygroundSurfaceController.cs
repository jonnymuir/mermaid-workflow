using GovUk.Frontend.AspNetCore.Extensions.Validation;
using MermaidWorkflow.CMS.Models;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using System;
using Umbraco.Cms.Core.Cache;
using Umbraco.Cms.Core.Logging;
using Umbraco.Cms.Core.Models.PublishedContent;
using Umbraco.Cms.Core.Routing;
using Umbraco.Cms.Core.Services;
using Umbraco.Cms.Core.Web;
using Umbraco.Cms.Infrastructure.Persistence;
using Umbraco.Cms.Web.Common.Filters;
using Umbraco.Cms.Web.Website.Controllers;

namespace MermaidWorkflow.CMS.Controllers;

public class PlaygroundSurfaceController : SurfaceController
{
    public PlaygroundSurfaceController(IUmbracoDatabaseFactory umbracoDatabaseFactory,
        IUmbracoContextAccessor umbracoContextAccessor,
        ServiceContext context,
        AppCaches appCaches,
        IProfilingLogger profilingLogger,
        IPublishedUrlProvider publishedUrlProvider
        )
        : base(umbracoContextAccessor, umbracoDatabaseFactory, context, appCaches, profilingLogger, publishedUrlProvider)
    {
    }

    [HttpPost]
    [ValidateAntiForgeryToken]
    [ValidateUmbracoFormRouteString]
    [ModelType(typeof(PlaygroundViewModel))]
    public IActionResult Index(PlaygroundViewModel viewModel)
    {
        if (ModelState.IsValid)
        {
            // Call the processing API and get the result back.
        }

        return View("Playground", viewModel);
    }

}
