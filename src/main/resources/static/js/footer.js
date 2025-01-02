let $footerInfo = document.querySelector("#footer_info");
let $footerGit = document.querySelector("#footer_git");

$footerInfo.addEventListener("click", () => {
    const anchor = document.querySelector("#footer_info_anchor");
    if (anchor) {
        anchor.click();
    } else {
        console.error(`Anchor tag with id is not found.`);
    }
});

$footerGit.addEventListener("click", () => {
    const anchor = document.querySelector("#footer_git_anchor");
    if (anchor) {
        anchor.click();
    } else {
        console.error(`Anchor tag with id is not found.`);
    }
});