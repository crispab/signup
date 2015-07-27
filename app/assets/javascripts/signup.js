
/*
To be called from <img onerror='imgError(this);' .../>
 */
function imgError(image) {
  image.onerror = "";
  image.src = "/assets/images/noimage.png";
  return true;
}

