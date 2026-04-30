import { PlayCircle } from 'lucide-react';

const Logo = ({ className = "" }: { className?: string }) => {
  return (
    <div className={`flex items-center gap-2 font-black tracking-tighter uppercase ${className}`}>
      <div className="relative flex items-center justify-center">
        <PlayCircle size={32} className="text-primary fill-primary/20" />
        <div className="absolute inset-0 bg-primary/20 blur-lg rounded-full animate-pulse"></div>
      </div>
      <span className="text-2xl md:text-3xl bg-gradient-to-r from-white via-white to-gray-500 bg-clip-text text-transparent">
        StreamSphere
      </span>
    </div>
  );
};

export default Logo;
