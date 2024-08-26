document.addEventListener('DOMContentLoaded', function () {
  const dropdownButton = document.querySelector('#dropdownMenuButton');
  const dropdownMenu = document.querySelector('#interestList');
  const checkboxes = dropdownMenu.querySelectorAll('input[type="checkbox"]');
  let selectedItems = [];

  dropdownButton.addEventListener('click', function () {
    dropdownMenu.style.display = dropdownMenu.style.display === 'block' ? 'none' : 'block';
  });

  document.addEventListener('click', function (event) {
    if (!dropdownButton.contains(event.target) && !dropdownMenu.contains(event.target)) {
      dropdownMenu.style.display = 'none';
    }
  });

  checkboxes.forEach(checkbox => {
    checkbox.addEventListener('change', function () {
      if (this.checked && selectedItems.length >= 3) {
        this.checked = false;
        alert('You can select a maximum of 3 interests.');
        return;
      }

      selectedItems = Array.from(checkboxes)
        .filter(cb => cb.checked)
        .map(cb => cb.value);

      updateDropdownText();
    });
  });

  function updateDropdownText() {
    dropdownButton.textContent = selectedItems.length === 0 ? 'Select Area of Interest' : selectedItems.join(', ');
  }
});
