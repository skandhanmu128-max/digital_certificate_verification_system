// Digital Certificate Verification System Client JS
document.addEventListener('DOMContentLoaded', () => {
    // Tooltip initialization
    const tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
    tooltipTriggerList.map(tooltipTriggerEl => new bootstrap.Tooltip(tooltipTriggerEl));
});

function copyToClipboard(text, buttonElement) {
    if (navigator.clipboard && window.isSecureContext) {
        navigator.clipboard.writeText(text).then(() => {
            showCopyFeedback(buttonElement);
        }).catch(err => {
            console.error('Failed to copy text: ', err);
            fallbackCopy(text, buttonElement);
        });
    } else {
        fallbackCopy(text, buttonElement);
    }
}

function fallbackCopy(text, buttonElement) {
    const textArea = document.createElement("textarea");
    textArea.value = text;
    textArea.style.position = "fixed";
    document.body.appendChild(textArea);
    textArea.focus();
    textArea.select();
    try {
        document.execCommand('copy');
        showCopyFeedback(buttonElement);
    } catch (err) {
        console.error('Fallback copy failed: ', err);
    }
    document.body.removeChild(textArea);
}

function showCopyFeedback(buttonElement) {
    if (!buttonElement) return;
    const originalText = buttonElement.innerHTML;
    buttonElement.innerHTML = '<i class="bi bi-check2"></i> Copied!';
    buttonElement.classList.remove('btn-outline-custom', 'btn-outline-primary');
    buttonElement.classList.add('btn-success');
    setTimeout(() => {
        buttonElement.innerHTML = originalText;
        buttonElement.classList.remove('btn-success');
        buttonElement.classList.add('btn-outline-custom');
    }, 2000);
}
