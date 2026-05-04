import { Link, Navigate } from 'react-router-dom';
import { useSelector } from 'react-redux';
import type { RootState } from '../app/store';
import { PlayCircle, ChevronRight } from 'lucide-react';

const Landing = () => {
  const { isAuthenticated } = useSelector((state: RootState) => state.auth);

  if (isAuthenticated) {
    return <Navigate to="/home" />;
  }

  return (
    <div className="relative min-h-screen flex flex-col bg-black">
      {/* Background Image with Overlay */}
      <div className="absolute inset-0 z-0 bg-gray-900 bg-cover bg-center opacity-40"></div>
      <div className="absolute inset-0 z-0 bg-gradient-to-b from-black/80 via-black/40 to-black/80"></div>
      
      {/* Minimal Navbar */}
      <nav className="relative z-10 py-6 px-8 flex items-center justify-between">
        <div className="text-primary text-3xl md:text-4xl font-bold uppercase tracking-tighter flex items-center gap-2">
          <PlayCircle size={40} />
          StreamSphere
        </div>
        <Link to="/login" className="bg-primary hover:bg-red-700 text-white font-bold py-2 px-6 rounded transition">
          Sign In
        </Link>
      </nav>

      {/* Hero Content */}
      <main className="relative z-10 flex-1 flex flex-col items-center justify-center text-center px-4 py-20">
        <h1 className="text-5xl md:text-7xl font-extrabold text-white mb-6 max-w-4xl drop-shadow-lg">
          Unlimited movies, TV shows, and more.
        </h1>
        <p className="text-2xl md:text-3xl text-white mb-10 drop-shadow-md">
          Watch anywhere. Cancel anytime.
        </p>
        <p className="text-xl text-white mb-6 drop-shadow-md">
          Ready to watch? Create an account to start your membership.
        </p>
        
        <Link to="/register" className="flex items-center gap-2 bg-primary hover:bg-red-700 text-white text-2xl font-bold py-4 px-10 rounded transition">
          Get Started <ChevronRight size={32} />
        </Link>
      </main>
    </div>
  );
};

export default Landing;
