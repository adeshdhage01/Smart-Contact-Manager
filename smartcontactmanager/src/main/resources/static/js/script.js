console.log("This is the script file");

// Toggle sidebar visibility
window.toggleSidebar = function () {
  const sidebar = document.querySelector(".sidebar");
  const content = document.querySelector(".content");

  const isHidden = getComputedStyle(sidebar).display === "none";

  if (isHidden) {
    sidebar.style.display = "block";
    content.style.marginLeft = "20%";
  } else {
    sidebar.style.display = "none";
    content.style.marginLeft = "0";
  }
};
