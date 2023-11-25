//------------------------------------------------------------------------------
// <auto-generated>
//   This code was generated by a tool.
//
//    Umbraco.ModelsBuilder.Embedded v12.3.1+80fac86
//
//   Changes to this file will be lost if the code is regenerated.
// </auto-generated>
//------------------------------------------------------------------------------

using System;
using System.Linq.Expressions;
using Umbraco.Cms.Core.Models.PublishedContent;
using Umbraco.Cms.Core.PublishedCache;
using Umbraco.Cms.Infrastructure.ModelsBuilder;
using Umbraco.Cms.Core;
using Umbraco.Extensions;

namespace MermaidWorkflow.CMS.Models
{
	// Mixin Content Type with alias "govukValidationText"
	/// <summary>Validation (Text)</summary>
	public partial interface IGovukValidationText : IPublishedElement
	{
		/// <summary>Compare to another field</summary>
		[global::System.CodeDom.Compiler.GeneratedCodeAttribute("Umbraco.ModelsBuilder.Embedded", "12.3.1+80fac86")]
		[global::System.Diagnostics.CodeAnalysis.MaybeNull]
		string ErrorMessageCompare { get; }

		/// <summary>Email address</summary>
		[global::System.CodeDom.Compiler.GeneratedCodeAttribute("Umbraco.ModelsBuilder.Embedded", "12.3.1+80fac86")]
		[global::System.Diagnostics.CodeAnalysis.MaybeNull]
		string ErrorMessageEmail { get; }

		/// <summary>Minimum and maximum length</summary>
		[global::System.CodeDom.Compiler.GeneratedCodeAttribute("Umbraco.ModelsBuilder.Embedded", "12.3.1+80fac86")]
		[global::System.Diagnostics.CodeAnalysis.MaybeNull]
		string ErrorMessageLength { get; }

		/// <summary>Maximum length</summary>
		[global::System.CodeDom.Compiler.GeneratedCodeAttribute("Umbraco.ModelsBuilder.Embedded", "12.3.1+80fac86")]
		[global::System.Diagnostics.CodeAnalysis.MaybeNull]
		string ErrorMessageMaxLength { get; }

		/// <summary>Minimum length</summary>
		[global::System.CodeDom.Compiler.GeneratedCodeAttribute("Umbraco.ModelsBuilder.Embedded", "12.3.1+80fac86")]
		[global::System.Diagnostics.CodeAnalysis.MaybeNull]
		string ErrorMessageMinLength { get; }

		/// <summary>Phone number</summary>
		[global::System.CodeDom.Compiler.GeneratedCodeAttribute("Umbraco.ModelsBuilder.Embedded", "12.3.1+80fac86")]
		[global::System.Diagnostics.CodeAnalysis.MaybeNull]
		string ErrorMessagePhone { get; }

		/// <summary>Pattern</summary>
		[global::System.CodeDom.Compiler.GeneratedCodeAttribute("Umbraco.ModelsBuilder.Embedded", "12.3.1+80fac86")]
		[global::System.Diagnostics.CodeAnalysis.MaybeNull]
		string ErrorMessageRegex { get; }
	}

	/// <summary>Validation (Text)</summary>
	[PublishedModel("govukValidationText")]
	public partial class GovukValidationText : PublishedElementModel, IGovukValidationText
	{
		// helpers
#pragma warning disable 0109 // new is redundant
		[global::System.CodeDom.Compiler.GeneratedCodeAttribute("Umbraco.ModelsBuilder.Embedded", "12.3.1+80fac86")]
		public new const string ModelTypeAlias = "govukValidationText";
		[global::System.CodeDom.Compiler.GeneratedCodeAttribute("Umbraco.ModelsBuilder.Embedded", "12.3.1+80fac86")]
		public new const PublishedItemType ModelItemType = PublishedItemType.Content;
		[global::System.CodeDom.Compiler.GeneratedCodeAttribute("Umbraco.ModelsBuilder.Embedded", "12.3.1+80fac86")]
		[return: global::System.Diagnostics.CodeAnalysis.MaybeNull]
		public new static IPublishedContentType GetModelContentType(IPublishedSnapshotAccessor publishedSnapshotAccessor)
			=> PublishedModelUtility.GetModelContentType(publishedSnapshotAccessor, ModelItemType, ModelTypeAlias);
		[global::System.CodeDom.Compiler.GeneratedCodeAttribute("Umbraco.ModelsBuilder.Embedded", "12.3.1+80fac86")]
		[return: global::System.Diagnostics.CodeAnalysis.MaybeNull]
		public static IPublishedPropertyType GetModelPropertyType<TValue>(IPublishedSnapshotAccessor publishedSnapshotAccessor, Expression<Func<GovukValidationText, TValue>> selector)
			=> PublishedModelUtility.GetModelPropertyType(GetModelContentType(publishedSnapshotAccessor), selector);
#pragma warning restore 0109

		private IPublishedValueFallback _publishedValueFallback;

		// ctor
		public GovukValidationText(IPublishedElement content, IPublishedValueFallback publishedValueFallback)
			: base(content, publishedValueFallback)
		{
			_publishedValueFallback = publishedValueFallback;
		}

		// properties

		///<summary>
		/// Compare to another field: Sets the error message displayed if the field is set to be the same as another (like when you're asked to re-enter an email address).
		///</summary>
		[global::System.CodeDom.Compiler.GeneratedCodeAttribute("Umbraco.ModelsBuilder.Embedded", "12.3.1+80fac86")]
		[global::System.Diagnostics.CodeAnalysis.MaybeNull]
		[ImplementPropertyType("errorMessageCompare")]
		public virtual string ErrorMessageCompare => GetErrorMessageCompare(this, _publishedValueFallback);

		/// <summary>Static getter for Compare to another field</summary>
		[global::System.CodeDom.Compiler.GeneratedCodeAttribute("Umbraco.ModelsBuilder.Embedded", "12.3.1+80fac86")]
		[return: global::System.Diagnostics.CodeAnalysis.MaybeNull]
		public static string GetErrorMessageCompare(IGovukValidationText that, IPublishedValueFallback publishedValueFallback) => that.Value<string>(publishedValueFallback, "errorMessageCompare");

		///<summary>
		/// Email address: Sets the message displayed if the field is set by the code to require an email address.
		///</summary>
		[global::System.CodeDom.Compiler.GeneratedCodeAttribute("Umbraco.ModelsBuilder.Embedded", "12.3.1+80fac86")]
		[global::System.Diagnostics.CodeAnalysis.MaybeNull]
		[ImplementPropertyType("errorMessageEmail")]
		public virtual string ErrorMessageEmail => GetErrorMessageEmail(this, _publishedValueFallback);

		/// <summary>Static getter for Email address</summary>
		[global::System.CodeDom.Compiler.GeneratedCodeAttribute("Umbraco.ModelsBuilder.Embedded", "12.3.1+80fac86")]
		[return: global::System.Diagnostics.CodeAnalysis.MaybeNull]
		public static string GetErrorMessageEmail(IGovukValidationText that, IPublishedValueFallback publishedValueFallback) => that.Value<string>(publishedValueFallback, "errorMessageEmail");

		///<summary>
		/// Minimum and maximum length: Sets the message displayed if the field is set by the code to require text between a minimum and maximum length.
		///</summary>
		[global::System.CodeDom.Compiler.GeneratedCodeAttribute("Umbraco.ModelsBuilder.Embedded", "12.3.1+80fac86")]
		[global::System.Diagnostics.CodeAnalysis.MaybeNull]
		[ImplementPropertyType("errorMessageLength")]
		public virtual string ErrorMessageLength => GetErrorMessageLength(this, _publishedValueFallback);

		/// <summary>Static getter for Minimum and maximum length</summary>
		[global::System.CodeDom.Compiler.GeneratedCodeAttribute("Umbraco.ModelsBuilder.Embedded", "12.3.1+80fac86")]
		[return: global::System.Diagnostics.CodeAnalysis.MaybeNull]
		public static string GetErrorMessageLength(IGovukValidationText that, IPublishedValueFallback publishedValueFallback) => that.Value<string>(publishedValueFallback, "errorMessageLength");

		///<summary>
		/// Maximum length: Sets the message displayed if the field is set by the code to require text of a maximum length.
		///</summary>
		[global::System.CodeDom.Compiler.GeneratedCodeAttribute("Umbraco.ModelsBuilder.Embedded", "12.3.1+80fac86")]
		[global::System.Diagnostics.CodeAnalysis.MaybeNull]
		[ImplementPropertyType("errorMessageMaxLength")]
		public virtual string ErrorMessageMaxLength => GetErrorMessageMaxLength(this, _publishedValueFallback);

		/// <summary>Static getter for Maximum length</summary>
		[global::System.CodeDom.Compiler.GeneratedCodeAttribute("Umbraco.ModelsBuilder.Embedded", "12.3.1+80fac86")]
		[return: global::System.Diagnostics.CodeAnalysis.MaybeNull]
		public static string GetErrorMessageMaxLength(IGovukValidationText that, IPublishedValueFallback publishedValueFallback) => that.Value<string>(publishedValueFallback, "errorMessageMaxLength");

		///<summary>
		/// Minimum length: Sets the message displayed if the field is set by the code to require text of a minimum length.
		///</summary>
		[global::System.CodeDom.Compiler.GeneratedCodeAttribute("Umbraco.ModelsBuilder.Embedded", "12.3.1+80fac86")]
		[global::System.Diagnostics.CodeAnalysis.MaybeNull]
		[ImplementPropertyType("errorMessageMinLength")]
		public virtual string ErrorMessageMinLength => GetErrorMessageMinLength(this, _publishedValueFallback);

		/// <summary>Static getter for Minimum length</summary>
		[global::System.CodeDom.Compiler.GeneratedCodeAttribute("Umbraco.ModelsBuilder.Embedded", "12.3.1+80fac86")]
		[return: global::System.Diagnostics.CodeAnalysis.MaybeNull]
		public static string GetErrorMessageMinLength(IGovukValidationText that, IPublishedValueFallback publishedValueFallback) => that.Value<string>(publishedValueFallback, "errorMessageMinLength");

		///<summary>
		/// Phone number: Sets the message displayed if the field is set by the code to require a phone number.
		///</summary>
		[global::System.CodeDom.Compiler.GeneratedCodeAttribute("Umbraco.ModelsBuilder.Embedded", "12.3.1+80fac86")]
		[global::System.Diagnostics.CodeAnalysis.MaybeNull]
		[ImplementPropertyType("errorMessagePhone")]
		public virtual string ErrorMessagePhone => GetErrorMessagePhone(this, _publishedValueFallback);

		/// <summary>Static getter for Phone number</summary>
		[global::System.CodeDom.Compiler.GeneratedCodeAttribute("Umbraco.ModelsBuilder.Embedded", "12.3.1+80fac86")]
		[return: global::System.Diagnostics.CodeAnalysis.MaybeNull]
		public static string GetErrorMessagePhone(IGovukValidationText that, IPublishedValueFallback publishedValueFallback) => that.Value<string>(publishedValueFallback, "errorMessagePhone");

		///<summary>
		/// Pattern: Sets the message displayed if the field is set by the code to require a regular expression pattern to be matched.
		///</summary>
		[global::System.CodeDom.Compiler.GeneratedCodeAttribute("Umbraco.ModelsBuilder.Embedded", "12.3.1+80fac86")]
		[global::System.Diagnostics.CodeAnalysis.MaybeNull]
		[ImplementPropertyType("errorMessageRegex")]
		public virtual string ErrorMessageRegex => GetErrorMessageRegex(this, _publishedValueFallback);

		/// <summary>Static getter for Pattern</summary>
		[global::System.CodeDom.Compiler.GeneratedCodeAttribute("Umbraco.ModelsBuilder.Embedded", "12.3.1+80fac86")]
		[return: global::System.Diagnostics.CodeAnalysis.MaybeNull]
		public static string GetErrorMessageRegex(IGovukValidationText that, IPublishedValueFallback publishedValueFallback) => that.Value<string>(publishedValueFallback, "errorMessageRegex");
	}
}