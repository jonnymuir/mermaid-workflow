using Umbraco.Cms.Web.Common.PublishedModels;

namespace MermaidWorkflow.CMS.Models;

public class PlaygroundViewModel
{
    public Playground? Page { get; set; }
    public string? Chart { get; set; }
    public string? Context { get; set; }
    public string? Mappings {get; set; }

}