import React from 'react';
import { PlayCircle, Loader2, AlertCircle, Film } from 'lucide-react';
import Navbar from '../components/Navbar';
import VideoCard from '../components/VideoCard';
import { useGetVideosQuery } from '../features/videos/videoApi';
import { useNavigate } from 'react-router-dom';

const Home: React.FC = () => {
  const navigate = useNavigate();
  const { data, isLoading, error, refetch } = useGetVideosQuery({
    status: 'COMPLETED',
    sort: 'createdAt,desc'
  });

  const handleVideoClick = (id: number) => {
    navigate(`/watch/${id}`);
  };

  return (
    <div className="min-h-screen bg-background text-white">
      <Navbar />
      
      <main>
        {/* Cinematic Hero Section */}
        <div className="relative h-[70vh] w-full flex items-center justify-start overflow-hidden border-b border-white/5 bg-[radial-gradient(circle_at_top_right,_var(--tw-gradient-stops))] from-primary/20 via-background to-background">
          <div className="absolute inset-0 z-0 bg-gradient-to-t from-background via-transparent to-background/50 opacity-90"></div>
          
          <div className="relative z-10 px-4 md:px-16 max-w-4xl">
            <h1 className="text-6xl md:text-8xl font-black mb-6 leading-tight tracking-tighter uppercase italic">
              Experience <br />
              <span className="text-primary not-italic drop-shadow-[0_0_20px_rgba(229,9,20,0.4)]">Cinematic</span> <br />
              Streaming
            </h1>
            <p className="text-xl md:text-2xl text-gray-400 mb-10 max-w-2xl leading-relaxed font-light">
              Join the new era of community-driven entertainment. High-quality content, streamed seamlessly.
            </p>
            <div className="flex flex-col sm:flex-row gap-4">
              <button 
                onClick={() => navigate('/upload')}
                className="px-10 py-4 bg-primary text-white font-black uppercase tracking-widest text-sm hover:bg-red-700 hover:scale-105 transition duration-300 rounded-sm"
              >
                Upload Content
              </button>
              <button 
                onClick={() => refetch()}
                className="px-10 py-4 bg-white/10 backdrop-blur-md text-white font-black uppercase tracking-widest text-sm border border-white/20 hover:bg-white/20 transition duration-300 rounded-sm"
              >
                Refresh Feed
              </button>
            </div>
          </div>

          <div className="hidden lg:block absolute -right-20 top-20 opacity-10">
            <PlayCircle size={600} strokeWidth={0.5} />
          </div>
        </div>

        {/* Video Discovery Section */}
        <section className="px-4 md:px-16 py-20 bg-background">
          <div className="flex items-center justify-between mb-12">
            <div className="flex items-center gap-4">
              <div className="h-10 w-2 bg-primary rounded-full"></div>
              <div>
                <h2 className="text-4xl font-black uppercase tracking-tighter italic">Discovery</h2>
                <p className="text-gray-500 text-xs font-bold tracking-[0.2em] uppercase mt-1">Recently processed films</p>
              </div>
            </div>
          </div>
          
          {isLoading ? (
            <div className="flex flex-col items-center justify-center py-20">
              <Loader2 className="text-primary animate-spin mb-4" size={48} />
              <p className="text-gray-500 font-bold tracking-widest uppercase text-sm">Loading your feed...</p>
            </div>
          ) : error ? (
            <div className="flex flex-col items-center justify-center py-20 bg-surface/30 border border-white/5 rounded-sm">
              <AlertCircle className="text-red-500 mb-4" size={48} />
              <p className="text-xl font-bold mb-2 text-white uppercase italic">Something went wrong</p>
              <p className="text-gray-500 mb-6 font-medium">Unable to fetch the latest videos at this time.</p>
              <button 
                onClick={() => refetch()}
                className="px-8 py-3 bg-white/5 border border-white/10 hover:bg-white/10 transition rounded-sm text-xs font-black uppercase tracking-widest"
              >
                Try Again
              </button>
            </div>
          ) : data?.content && data.content.length > 0 ? (
            <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-x-8 gap-y-12">
              {data.content.map((video) => (
                <VideoCard key={video.id} video={video} onClick={handleVideoClick} />
              ))}
            </div>
          ) : (
            <div className="flex flex-col items-center justify-center py-20 bg-surface/30 border border-white/5 rounded-sm text-center">
              <Film className="text-white/10 mb-6" size={80} />
              <p className="text-2xl font-black mb-2 uppercase italic text-white/50 tracking-tighter">No videos found</p>
              <p className="text-gray-500 max-w-xs font-medium leading-relaxed">
                Be the first to share your cinematic masterpiece with the world.
              </p>
              <button 
                onClick={() => navigate('/upload')}
                className="mt-8 px-8 py-3 bg-primary text-white text-xs font-black uppercase tracking-widest rounded-sm hover:bg-red-700 transition"
              >
                Upload Now
              </button>
            </div>
          )}
        </section>
      </main>
    </div>
  );
};

export default Home;
