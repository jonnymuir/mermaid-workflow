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
using Microsoft.Extensions.Configuration;
using System.Net.Http;
using System.Threading.Tasks;
using Newtonsoft.Json;
using Newtonsoft.Json.Linq;
using Microsoft.AspNetCore.Mvc.ModelBinding;

namespace MermaidWorkflow.CMS.Controllers;

public class PlaygroundSurfaceController : SurfaceController
{
    private readonly IHttpClientFactory httpClientFactory;
    private readonly IConfiguration configuration;
    private string baseUrl;

    public PlaygroundSurfaceController(IUmbracoDatabaseFactory umbracoDatabaseFactory,
        IUmbracoContextAccessor umbracoContextAccessor,
        ServiceContext context,
        AppCaches appCaches,
        IProfilingLogger profilingLogger,
        IPublishedUrlProvider publishedUrlProvider,
        IHttpClientFactory httpClientFactory,
        IConfiguration configuration
        )
        : base(umbracoContextAccessor, umbracoDatabaseFactory, context, appCaches, profilingLogger, publishedUrlProvider)
    {
        this.httpClientFactory = httpClientFactory;
        this.configuration = configuration;

        // Retrieve the URL and API key from appsettings.json
        this.baseUrl = this.configuration["MermaidWorkflowApi:BaseUrl"] ?? string.Empty;
    }

    [HttpPost]
    [ValidateAntiForgeryToken]
    [ValidateUmbracoFormRouteString]
    [ModelType(typeof(PlaygroundViewModel))]
    public async Task<IActionResult> Index(PlaygroundViewModel viewModel)
    {
        if (ModelState.IsValid)
        {
            var client = httpClientFactory.CreateClient();

            JObject contextJson = new();
            if (!string.IsNullOrWhiteSpace(viewModel.Context))
            {
                try
                {
                    contextJson = JObject.Parse(viewModel.Context);
                }
                catch (JsonReaderException ex)
                {
                    ModelState.AddModelError("Context", "The context data is not in a valid JSON format.");
                    ViewData["DetailedError"] = ex;
                    return View("Playground", viewModel); 
                }
            }

            var payload = new
            {
                chart = viewModel.Chart,
                context = contextJson,
                mappings = viewModel.Mappings
            };

            // Serialize the anonymous object to JSON
            var json = JsonConvert.SerializeObject(payload);
            var content = new StringContent(json, System.Text.Encoding.UTF8, "application/json");

            // Make the POST request
            var response = await client.PostAsync($"{baseUrl}/chart/process", content);

            if (response.IsSuccessStatusCode)
            {
                var responseContent = await response.Content.ReadAsStringAsync();
                // Assuming the response content is a JSON object that includes a "context" property
                var context = JObject.Parse(responseContent)["context"]?.ToString();

                viewModel.Context = context;
                ModelState.SetModelValue("Context", new ValueProviderResult(context));

            }
            else
            {
                var errorContent = await response.Content.ReadAsStringAsync();
                try
                {
                    var errorResponse = JsonConvert.DeserializeObject<dynamic>(errorContent);
                    var humanizedErrors = errorResponse.humanized;

                    // Add detailed error for access in the view
                    ViewData["DetailedError"] = errorContent;

                    foreach (var errorKey in humanizedErrors)
                    {
                        // Add human-readable errors to ModelState
                        ModelState.AddModelError(errorKey.Name, errorKey.Value[0].ToString());
                    }
                }
                catch (JsonException)
                {
                    // If parsing fails, add the raw error content
                    ModelState.AddModelError(string.Empty, "An unexpected error occurred.");
                    ViewData["DetailedError"] = errorContent;
                }
            }
        }

        // Return to the same view with the viewModel, which now includes the updated context or error message
        return View("Playground", viewModel);
    }
}
