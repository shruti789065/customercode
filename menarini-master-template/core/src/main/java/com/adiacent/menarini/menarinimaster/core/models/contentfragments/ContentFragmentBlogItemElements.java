package com.adiacent.menarini.menarinimaster.core.models.contentfragments;

public class ContentFragmentBlogItemElements {
    private ContentFragmentElementSingleValue date;
    private ContentFragmentElementSingleValue imageLink;
    private ContentFragmentElementValue category;

    private ContentFragmentElementSingleValue title;
    private ContentFragmentElementSingleValue pageLink;

    private ContentFragmentElementSingleValue pageLinkTarget;

    private ContentFragmentElementSingleValue imageLinkTarget;

    public ContentFragmentElementSingleValue getDate() {
        return date;
    }

    public void setDate(ContentFragmentElementSingleValue date) {
        this.date = date;
    }

    public ContentFragmentElementSingleValue getImageLink() {
        return imageLink;
    }

    public void setImageLink(ContentFragmentElementSingleValue imageLink) {
        this.imageLink = imageLink;
    }

    public ContentFragmentElementValue getCategory() {
        return category;
    }

    public void setCategory(ContentFragmentElementValue category) {
        this.category = category;
    }

    public ContentFragmentElementSingleValue getTitle() {
        return title;
    }

    public void setTitle(ContentFragmentElementSingleValue title) {
        this.title = title;
    }

    public ContentFragmentElementSingleValue getPageLink() {
        return pageLink;
    }

    public void setPageLink(ContentFragmentElementSingleValue pageLink) {
        this.pageLink = pageLink;
    }

    public void setImageLinkTarget(ContentFragmentElementSingleValue imageLinkTargetE) {
    }

    public ContentFragmentElementSingleValue getPageLinkTarget() {
        return pageLinkTarget;
    }

    public void setPageLinkTarget(ContentFragmentElementSingleValue pageLinkTarget) {
        this.pageLinkTarget = pageLinkTarget;
    }

    public ContentFragmentElementSingleValue getImageLinkTarget() {
        return imageLinkTarget;
    }
}
