export function getTooltipForTag(tag) {
    let toolTipText = "Click to view all messages related to this tag";
    if(tag.isPrimaryMessageId) {
        toolTipText = "Click to view this message and the child messages related to this message.";
    } else if(tag.isMessageCorrelationId) {
        toolTipText = "Click to view this message and the parent messages related to this message"
    }

    return toolTipText;
}