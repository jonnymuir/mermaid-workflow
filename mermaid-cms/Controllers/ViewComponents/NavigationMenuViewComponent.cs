using Microsoft.AspNetCore.Mvc;
using Umbraco.Cms.Web.Common.PublishedModels;
using Umbraco.Cms.Web.Common;
using Umbraco.Cms.Core.Web;
using Umbraco.Extensions;

namespace MermaidWorkflow.CMS.Controllers.ViewComponents;

public class NavigationMenuViewComponent : ViewComponent
{
    private readonly IUmbracoHelperAccessor umbracoHelperAccessor;

    public NavigationMenuViewComponent(IUmbracoHelperAccessor umbracoHelperAccessor)
    {
        this.umbracoHelperAccessor = umbracoHelperAccessor;
    }

    public IViewComponentResult Invoke()
    {
        if (!this.umbracoHelperAccessor.TryGetUmbracoHelper(out var umbracoHelper))
        {
            throw new InvalidOperationException("UmbracoHelper could not be retrieved. This may occur if the method is called outside of the Umbraco request pipeline, such as during application startup, in a background service, or before Umbraco has set up the necessary context.");
        }

        // Get the first visible root node - This will be the "Home". We assume the navigation is children of this
        var rootNode = umbracoHelper.ContentAtRoot().FirstOrDefault(x => x.IsVisible());

        // If the root node is null, the navigation can't be built.
        if (rootNode == null)
        {
            throw new InvalidOperationException("The root node for the navigation menu is not found or not visible.");
        }

        var menuItems = rootNode.Children.Where(x => x.IsVisible()).ToList();

        return View(menuItems); // This will look for a view named 'Default.cshtml' under '/Views/Shared/Components/NavigationMenu'
     }
}