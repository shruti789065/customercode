document.addEventListener("DOMContentLoaded", () => {
  const tabList = document.querySelector(".cmp-tabs__tablist");

  const getButton = (selector) => document.querySelector(selector);

  const createTabListItem = (button) => {
    if (!button) {
      return null;
    }

    const clonedButton = button.cloneNode(true);
    const listItem = document.createElement("li");

    listItem.classList.add("options-tab");

    listItem.appendChild(clonedButton);
    return listItem;
  };

  const addButtonToTabList = (buttonSelector) => {
    const button = getButton(buttonSelector);
    const tabListItem = createTabListItem(button);

    if (tabListItem) {
      tabList.appendChild(tabListItem);
    }
  };

  addButtonToTabList(".button-search .cmp-button");
  addButtonToTabList(".button-profile .cmp-button");
});
