// Muestra una sola sección a la vez, según el menú
function mostrar(){
  const vistas=document.querySelectorAll('.vista');
  const id=location.hash.slice(1)||vistas[0].id;
  vistas.forEach(v=>v.classList.toggle('activa',v.id===id));
  document.querySelectorAll('.side nav a').forEach(a=>a.classList.toggle('activa',a.getAttribute('href')==='#'+id));
  window.scrollTo(0,0);
}
if(document.querySelector('.vista')){window.addEventListener('hashchange',mostrar);mostrar();}
// Buscador: filtra las filas de la tabla mientras escribís
document.querySelectorAll('.buscar').forEach(i=>i.addEventListener('input',()=>{
  const q=i.value.toLowerCase();
  i.nextElementSibling.querySelectorAll('tr:not(:first-child)').forEach(r=>{r.style.display=r.textContent.toLowerCase().includes(q)?'':'none'});
}));
