window.selectMenu = () => {
  const path = window.location.pathname
  const items = document.getElementsByClassName("menu-link");
  let best = [0, undefined];
  for (item of items) {
    const link = item.pathname;
    if (path === link) best = [1, item]
    if (path === link || path === '/') break
    let i = 0;
    let slashes = 0
    while (i < path.length && i < link.length && path[i] === link[i]) {
      if (link[i] === '/') slashes++
      i++
    }
    if (path !== '/' && i > best[0] && slashes > 1) {
      best = [i, item]
    }
  }
  for (item of items) {
    if (item === best[1]) {
      item.parentNode.classList.add("selected")
    } else {
      item.parentNode.classList.remove("selected")
    }
  }
}

window.selectMenu()
delete window.selectMenu
