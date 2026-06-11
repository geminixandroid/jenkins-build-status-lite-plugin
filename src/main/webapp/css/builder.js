const builder = document.querySelector(".ebs-builder");
const type = document.querySelector("select[name='type']");
const style = document.querySelector("select[name='style']");
const subject = document.querySelector("input[name='subject']");
const status = document.querySelector("input[name='status']");
const imagePreview = document.querySelector("#image-preview");
const copyButton = document.querySelector(".jenkins-copy-button");
const imageUrlInput = document.querySelector(".jenkins-quote input");

[type, style, subject, status].forEach(element => {
    element.addEventListener('change', () => {
        generateUrl();
    })
})

function generateUrl() {
    const badgeUrl = builder.dataset.publicBadgeUrl;

    const searchParams = new URLSearchParams();
    if (style.value && style.value !== 'flat') {
        searchParams.append("style", style.value);
    }
    if (subject.value) {
        searchParams.append("subject", subject.value);
    }
    if (status.value) {
        searchParams.append("status", status.value);
    }

    let url = badgeUrl;
    if (searchParams.toString()) {
        url += (url.includes('?') ? '&' : '?') + searchParams.toString();
    }

    const urls = {
        'markdown': `[![Build Status](${url})](${url})`,
        'image': url,
        'html': `<img alt="Badge" src="${url}" />`,
        'asciidoc': `image:${url}[]`,
        'confluence': `[!${url}!]`,
        'xwiki': `[[image:${url}]]`,
        'rdoc': `{<img alt="Badge" src='${url}' />}`,
        'textile': `!${url}!`,
        'bitbucket': `[Build Status](${url})`,
        'text': url
    }

    if (document.startViewTransition) {
        document.startViewTransition(() => {
            imagePreview.src = url;
            imageUrlInput.value = urls[type.value];
        });
    } else {
        imagePreview.src = url;
        imageUrlInput.value = urls[type.value];
    }

    copyButton.setAttribute('text', urls[type.value]);
}

generateUrl();
