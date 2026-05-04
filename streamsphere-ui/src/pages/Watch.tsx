import React, { useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { useGetVideoByIdQuery, useGetVideosQuery } from '../features/videos/videoApi';
import Navbar from '../components/Navbar';
import { Loader2, AlertCircle, Calendar, FileVideo, ChevronLeft, Info } from 'lucide-react';

const Watch: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const videoId = parseInt(id || '0');

  const { data: video, isLoading, error } = useGetVideoByIdQuery(videoId);
  const { data: recommendations } = useGetVideosQuery({ 
    status: 'COMPLETED',
    size: 8 
  });

  const [selectedResolution, setSelectedResolution] = useState<string | null>(null);

  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleDateString(undefined, { 
      year: 'numeric', 
      month: 'long', 
      day: 'numeric' 
    });
  };

  if (isLoading) {
    return (
      <div className="min-h-screen bg-background flex flex-col">
        <Navbar />
        <div className="flex-1 flex items-center justify-center">
          <Loader2 className="text-primary animate-spin" size={48} />
        </div>
      </div>
    );
  }

  if (error || !video) {
    return (
      <div className="min-h-screen bg-background flex flex-col">
        <Navbar />
        <div className="flex-1 flex flex-col items-center justify-center p-4 text-center">
          <AlertCircle className="text-red-500 mb-4" size={64} />
          <h2 className="text-3xl font-black uppercase italic mb-2">Video Not Found</h2>
          <p className="text-gray-500 mb-8 max-w-md">
            The video you're looking for doesn't exist or has been removed.
          </p>
          <button 
            onClick={() => navigate('/')}
            className="flex items-center gap-2 px-6 py-3 bg-white/5 border border-white/10 hover:bg-white/10 transition rounded-sm text-xs font-black uppercase tracking-widest"
          >
            <ChevronLeft size={16} /> Back to Home
          </button>
        </div>
      </div>
    );
  }

  const apiBaseUrl = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080';
  const streamUrl = `${apiBaseUrl}/api/videos/${id}/stream${selectedResolution ? `?resolution=${selectedResolution}` : ''}`;

  return (
    <div className="min-h-screen bg-background text-white">
      <Navbar />
      
      <main className="px-4 md:px-8 py-8 lg:flex lg:gap-8 max-w-[1800px] mx-auto">
        {/* Main Content: Player and Details */}
        <div className="lg:flex-1">
          {/* Video Player Container */}
          <div className="aspect-video bg-black rounded-sm overflow-hidden border border-white/5 shadow-2xl relative group">
            <video 
              key={streamUrl}
              controls 
              autoPlay
              crossOrigin="anonymous"
              className="w-full h-full"
              poster="/hero.png"
              src={streamUrl}
            >
              Your browser does not support the video tag.
            </video>
          </div>

          {/* Video Metadata */}
          <div className="mt-8">
            <div className="flex items-start justify-between gap-4">
              <div>
                <h1 className="text-3xl font-black tracking-tighter uppercase italic leading-tight">
                  {video.title}
                </h1>
                <div className="flex flex-wrap items-center gap-6 mt-4 text-gray-500 text-xs font-bold tracking-widest uppercase">
                  <span className="flex items-center gap-2">
                    <Calendar size={14} className="text-primary" />
                    {formatDate(video.createdAt)}
                  </span>
                  <span className="flex items-center gap-2">
                    <FileVideo size={14} className="text-primary" />
                    {video.resolutions.length > 0 ? `${video.resolutions.length} Resolutions Available` : 'Raw Quality'}
                  </span>
                  <span className="px-2 py-0.5 bg-primary/10 text-primary border border-primary/20 rounded-sm">
                    {video.status}
                  </span>
                  <span className="px-2 py-0.5 bg-surface text-gray-400 border border-white/5 rounded-sm">
                    {video.minimumSubscriptionTier}
                  </span>
                </div>
              </div>
            </div>

            {/* Resolutions Section */}
            {video.resolutions && video.resolutions.length > 0 && (
              <div className="mt-8">
                <h2 className="text-sm font-black uppercase tracking-widest mb-4 flex items-center gap-2 text-white/40">
                  <Info size={16} className="text-primary" />
                  Available Resolutions
                </h2>
                <div className="flex gap-4 flex-wrap">
                  <button 
                    onClick={() => setSelectedResolution(null)}
                    className={`px-4 py-2 rounded-sm border text-[10px] font-black uppercase tracking-widest transition ${
                      selectedResolution === null 
                        ? 'bg-primary border-primary text-white' 
                        : 'bg-white/5 hover:bg-white/10 border-white/10'
                    }`}
                  >
                    Original
                  </button>
                  {video.resolutions.map((res) => (
                    <button 
                      key={res.resolution}
                      onClick={() => setSelectedResolution(res.resolution)}
                      className={`px-4 py-2 rounded-sm border text-[10px] font-black uppercase tracking-widest transition ${
                        selectedResolution === res.resolution 
                          ? 'bg-primary border-primary text-white' 
                          : 'bg-white/5 hover:bg-white/10 border-white/10'
                      }`}
                    >
                      {res.resolution}
                    </button>
                  ))}
                </div>
              </div>
            )}

            <div className="mt-8 p-6 bg-surface/50 border border-white/5 rounded-sm">
              <h3 className="text-sm font-black uppercase tracking-widest mb-4 text-white/40">Description</h3>
              <p className="text-gray-300 leading-relaxed font-light whitespace-pre-wrap">
                {video.description || "No description provided for this cinematic experience."}
              </p>
            </div>
          </div>
        </div>

        {/* Sidebar: Recommendations */}
        <div className="mt-12 lg:mt-0 lg:w-[400px]">
          <div className="flex items-center gap-3 mb-6">
            <div className="h-6 w-1.5 bg-primary rounded-full"></div>
            <h2 className="text-xl font-black uppercase tracking-tighter italic">Up Next</h2>
          </div>
          
          <div className="flex flex-col gap-8">
            {recommendations?.content
              ?.filter(v => v.id !== video.id)
              ?.slice(0, 6)
              ?.map((rec) => (
                <div 
                  key={rec.id} 
                  className="flex gap-4 cursor-pointer group"
                  onClick={() => navigate(`/watch/${rec.id}`)}
                >
                  <div className="w-40 aspect-video bg-surface rounded-sm border border-white/10 flex-shrink-0 relative overflow-hidden transition-all duration-300 group-hover:border-primary/50">
                    <div className="absolute inset-0 flex items-center justify-center">
                      <Loader2 className="text-white/5 animate-pulse" size={24} />
                    </div>
                  </div>
                  <div className="flex-1 min-w-0">
                    <h4 className="font-bold text-sm text-white group-hover:text-primary transition line-clamp-2 tracking-tight">
                      {rec.title}
                    </h4>
                    <p className="text-gray-500 text-[10px] mt-2 uppercase font-black tracking-widest">
                      {formatDate(rec.createdAt)}
                    </p>
                  </div>
                </div>
              ))}
          </div>
        </div>
      </main>
    </div>
  );
};

export default Watch;
