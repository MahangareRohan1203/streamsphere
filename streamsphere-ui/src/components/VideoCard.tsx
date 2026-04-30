import React from 'react';
import { PlayCircle, Clock, CheckCircle, AlertCircle, Loader2 } from 'lucide-react';
import type { VideoResponse } from '../features/videos/videoApi';

interface VideoCardProps {
  video: VideoResponse;
  onClick?: (id: number) => void;
}

const VideoCard: React.FC<VideoCardProps> = ({ video, onClick }) => {
  const getStatusIcon = (status: string) => {
    switch (status) {
      case 'COMPLETED':
        return <CheckCircle size={14} className="text-green-500" />;
      case 'FAILED':
        return <AlertCircle size={14} className="text-red-500" />;
      case 'PROCESSING':
        return <Loader2 size={14} className="text-blue-500 animate-spin" />;
      default:
        return <Clock size={14} className="text-gray-500" />;
    }
  };

  const formatDate = (dateString: string) => {
    const date = new Date(dateString);
    return date.toLocaleDateString(undefined, { 
      year: 'numeric', 
      month: 'short', 
      day: 'numeric' 
    });
  };

  return (
    <div 
      className="group cursor-pointer" 
      onClick={() => onClick && onClick(video.id)}
    >
      <div className="aspect-video bg-surface rounded-sm border border-white/10 flex items-center justify-center relative overflow-hidden transition-all duration-500 group-hover:border-primary/50 group-hover:scale-[1.02]">
        {/* Placeholder for Thumbnail */}
        <div className="absolute inset-0 bg-gradient-to-br from-primary/10 to-transparent"></div>
        <PlayCircle className="text-white/20 group-hover:text-primary transition-colors duration-300 relative z-10" size={48} />
        
        <div className="absolute inset-0 bg-gradient-to-t from-black/80 via-transparent to-transparent opacity-0 group-hover:opacity-100 transition-opacity duration-300 flex items-end p-4">
          <span className="text-[10px] font-black tracking-widest uppercase bg-primary px-2 py-0.5 rounded-sm">
            {video.status === 'COMPLETED' ? 'Ready to Stream' : video.status}
          </span>
        </div>
      </div>
      
      <div className="mt-4">
        <h3 className="font-bold text-lg group-hover:text-primary transition tracking-tight line-clamp-1">
          {video.title}
        </h3>
        <div className="flex items-center gap-3 mt-1 text-gray-500 text-xs font-medium uppercase tracking-wider">
          <span className="flex items-center gap-1">
            {getStatusIcon(video.status)}
            {video.status}
          </span>
          <span className="h-1 w-1 bg-gray-700 rounded-full"></span>
          <span className="flex items-center gap-1">
            <Clock size={12} />
            {formatDate(video.createdAt)}
          </span>
        </div>
      </div>
    </div>
  );
};

export default VideoCard;
