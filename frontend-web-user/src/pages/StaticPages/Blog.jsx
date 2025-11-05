// src/pages/Blog.jsx
import React, { useState } from "react";

const initialPosts = [
  {
    id: 1,
    title: "MatchPET: Bandanas Solidarias y Atrevia extienden campaña",
    excerpt: "La iniciativa para apoyar la misión de MatchPET y que más de 750 perritos encuentren un hogar lleno de amor...",
    author: "Vielka Bahamonde",
    date: "29 sept",
    readTime: "3 min",
    likes: 3,
    comments: 16,
    image: "https://static.wixstatic.com/media/691d5d_754d8d2da3e741cb8ceaaaf5be82e319~mv2.png/v1/fill/w_552,h_414,fp_0.50_0.50,lg_1,q_95,enc_avif,quality_auto/691d5d_754d8d2da3e741cb8ceaaaf5be82e319~mv2.png"
  },
  {
    id: 2,
    title: "MatchPET: Cachorros abandonados esperan encontrar una familia",
    excerpt: "En el Perú, miles de perros son abandonados cada año y muchos lo viven en sus etapas más vulnerables...",
    author: "Vielka Bahamonde",
    date: "29 sept",
    readTime: "3 min",
    likes: 6,
    comments: 15,
    image: "https://images.unsplash.com/photo-1558788353-f76d92427f16?auto=format&fit=crop&w=800&q=60"
  },
  {
    id: 3,
    title: "MatchPET y Alianza Lima: Unidos por la adopción",
    excerpt: "Una emotiva campaña que busca transformar la vida de perritos a través de la adopción responsable...",
    author: "Renzo Garcés",
    date: "28 sept",
    readTime: "4 min",
    likes: 12,
    comments: 20,
    image: "https://static.wixstatic.com/media/691d5d_7efa8b0fcdf54acbb79aa583973b7ca6~mv2.png/v1/fill/w_821,h_616,fp_0.50_0.50,lg_1,q_95,enc_avif,quality_auto/691d5d_7efa8b0fcdf54acbb79aa583973b7ca6~mv2.png"
  },
  {
    id: 4,
    title: "Consultorio WUF: ¿Cómo mantener hidratado a tu gato?",
    excerpt: "A diferencia de los perros, los gatos están más predispuestos a tener problemas urinarios y...",
    author: "MatchPET",
    date: "2 sept",
    readTime: "3 min",
    likes: 8,
    comments: 10,
    image: "https://static.wixstatic.com/media/691d5d_aff03dac0410467993c7e591dc808d2a~mv2.png/v1/fill/w_598,h_449,fp_0.50_0.50,lg_1,q_95,enc_avif,quality_auto/691d5d_aff03dac0410467993c7e591dc808d2a~mv2.png"
  },
  {
    id: 5,
    title: "MatchPET y Circular promueven el cuidado de mascotas",
    excerpt: "La comunidad de Pachacámac cuenta ahora con puficanes reciclados para fomentar el respeto por las mascotas...",
    author: "MatchPET",
    date: "2 sept",
    readTime: "2 min",
    likes: 5,
    comments: 8,
    image: "https://images.unsplash.com/photo-1592194996308-7b43878e84a6?auto=format&fit=crop&w=800&q=60"
  },
  {
    id: 6,
    title: "Cuatro historias, un mismo objetivo: ¡correr por los perritos! ",
    excerpt: "Runners solidarios recaudan fondos para 8 ONGs; MatchPET participa activamente...",
    author: "MatchPET",
    date: "1 sept",
    readTime: "3 min",
    likes: 9,
    comments: 12,
    image: "https://static.wixstatic.com/media/8af33b_cd21aef5df25431786a16b7fc3c76344~mv2.avif/v1/fill/w_1067,h_800,al_c,q_90,enc_avif,quality_auto/8af33b_cd21aef5df25431786a16b7fc3c76344~mv2.avif"
  },
  {
    id: 7,
    title: "Consultorio MatchPET: Cómo cuidar la higiene dental de tu mascota",
    excerpt: "La veterinaria Carolina Taboada nos explica cómo mantener la salud dental de tus peludos...",
    author: "Vielka Bahamonde",
    date: "29 ago",
    readTime: "3 min",
    likes: 4,
    comments: 6,
    image: "https://static.wixstatic.com/media/691d5d_8c30f4e7db754046a6154f1e335d14f2~mv2.png/v1/fill/w_707,h_530,fp_0.50_0.50,lg_1,q_95,enc_avif,quality_auto/691d5d_8c30f4e7db754046a6154f1e335d14f2~mv2.png"
  },
  {
    id: 8,
    title: "MatchPET: Historia de ‘Menta’, la perrita adoptada por Andrés Wiese",
    excerpt: "Conoce la historia de Menta y cómo la adopción cambió su vida y la de su familia...",
    author: "Vielka Bahamonde",
    date: "29 ago",
    readTime: "2 min",
    likes: 7,
    comments: 9,
    image: "https://images.unsplash.com/photo-1574158622682-e40e69881006?auto=format&fit=crop&w=800&q=60"
  },
];

const Blog = () => {
  const [posts, setPosts] = useState(
    initialPosts.map(post => ({ ...post, liked: false, animate: false }))
  );

  const toggleLike = (id) => {
    setPosts(posts.map(post => {
      if (post.id === id) {
        const isLiked = !post.liked;
        return {
          ...post,
          liked: isLiked,
          likes: isLiked ? post.likes + 1 : post.likes - 1,
          animate: true
        };
      }
      return post;
    }));

    setTimeout(() => {
      setPosts(prevPosts =>
        prevPosts.map(post => ({ ...post, animate: false }))
      );
    }, 300);
  };

  return (
    <div className="bg-gradient-to-br from-[#D7F0F7] via-[#79B5C0]/60 to-[#407581]/10 min-h-screen px-6 py-12">
      <h1 className="text-center text-5xl font-serif italic font-extrabold text-[#407581] mb-6">Blog MatchPet</h1>
      <div className="max-w-7xl mx-auto px-4">
        <div className="grid gap-8 md:grid-cols-2 lg:grid-cols-3 justify-items-center">
          {posts.map(post => (
            <div key={post.id} className="w-full max-w-sm border rounded-xl overflow-hidden hover:shadow-xl transition bg-white">
              <img src={post.image} alt={post.title} className="w-full h-52 object-cover"/>
              <div className="p-5">
                <h2 className="text-xl font-semibold mb-2 text-gray-800">{post.title}</h2>
                <p className="text-gray-600 mb-3">{post.excerpt}</p>
                <div className="flex justify-between text-sm text-gray-500 mb-3">
                  <span>{post.author}</span>
                  <span>{post.date} · {post.readTime}</span>
                </div>
                <div className="flex gap-4 text-sm">
                  <button 
                    onClick={() => toggleLike(post.id)}
                    className={`flex items-center gap-1 transition transform ${
                      post.liked ? "text-red-500" : "text-gray-500"
                    } ${post.animate ? "scale-125" : ""}`}
                  >
                    {post.liked ? "❤️" : "🤍"} {post.likes}
                  </button>
                  <span className="text-gray-500 flex items-center gap-1">💬 {post.comments}</span>
                </div>
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
};

export default Blog;
