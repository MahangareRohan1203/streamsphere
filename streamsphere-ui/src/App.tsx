import { BrowserRouter as Router, Routes, Route, Navigate, useNavigate } from 'react-router-dom';
import { useSelector } from 'react-redux';
import type { RootState } from './app/store';
import Landing from './pages/Landing';
import Login from './pages/Login';
import Register from './pages/Register';
import Upload from './pages/Upload';
import Subscribe from './pages/Subscribe';
import Watch from './pages/Watch';
import Navbar from './components/Navbar';
import { useGetVideosQuery } from './features/videos/videoApi';
import { Lock } from 'lucide-react';

// Helper to check if user has access.
// Rationale: Performs client-side tier validation by comparing the numeric weight 
// of the user's current tier against the video's requirement.
// This provides immediate UI feedback (locks/unlocks) before any API calls are made.
const hasAccess = (userTier: string | null, requiredTier: string) => {
  const tiers: Record<string, number> = { 'FREE': 0, 'PREMIUM': 1, 'GOLD': 2 };
  const uWeight = tiers[userTier || 'FREE'] ?? 0;
  const rWeight = tiers[requiredTier] ?? 0;
  return uWeight >= rWeight;
};

// Real Home Component
const Home = () => {
  const { data: videos, isLoading, error } = useGetVideosQuery();
  const { currentTier } = useSelector((state: RootState) => state.auth);
  const navigate = useNavigate();

  return (
    <>
      <Navbar />
      <div className="p-8 max-w-7xl mx-auto">
        <header className="mb-10">
          <h1 className="text-4xl font-extrabold text-white">Welcome Back!</h1>
          <p className="text-lg text-gray-400 mt-2">Ready to watch something new?</p>
        </header>

        <section>
          <h2 className="text-2xl font-bold mb-6">Recently Uploaded</h2>
          
          {isLoading && (
            <div className="flex justify-center items-center h-40">
              <div className="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-primary"></div>
            </div>
          )}

          {error && (
            <div className="bg-red-900/20 border border-red-500 text-red-100 p-4 rounded">
              Error loading videos. Please try again later.
            </div>
          )}

          {!isLoading && !error && videos && videos.content.length === 0 && (
            <div className="aspect-video bg-surface rounded-md border border-gray-800 flex items-center justify-center group cursor-pointer hover:scale-105 transition duration-300">
              <p className="text-gray-500 group-hover:text-primary transition">No videos yet. Upload one!</p>
            </div>
          )}

          {!isLoading && !error && videos && videos.content.length > 0 && (
            <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-6">
              {videos.content.map((video) => {
                const locked = !hasAccess(currentTier, video.minimumSubscriptionTier);
                return (
                  <div 
                    key={video.id} 
                    className="bg-surface rounded-md border border-gray-800 overflow-hidden group cursor-pointer hover:scale-105 transition duration-300 relative"
                    // Locked Logic: Prevents navigation if the user doesn't meet the tier requirement.
                    // This is the primary UX gatekeeper for premium content.
                    onClick={() => !locked && navigate(`/watch/${video.id}`)}
                  >
                    <div className="aspect-video bg-gray-800 flex items-center justify-center relative">
                      <span className="text-4xl">🎬</span>
                      <div className="absolute inset-0 bg-black/40 opacity-0 group-hover:opacity-100 flex items-center justify-center transition">
                        {locked ? (
                          <div className="bg-black/60 p-4 rounded-lg text-center">
                            <Lock className="mx-auto text-primary mb-2" size={24} />
                            <span className="text-sm font-bold uppercase">{video.minimumSubscriptionTier} ONLY</span>
                          </div>
                        ) : (
                          <button className="bg-primary text-white px-4 py-2 rounded-full font-bold">Watch</button>
                        )}
                      </div>
                    </div>
                    <div className="p-4">
                      <div className="flex justify-between items-start gap-2">
                        <h3 className="font-bold text-lg truncate flex-1">{video.title}</h3>
                        {locked && <Lock size={16} className="text-primary mt-1 shrink-0" />}
                      </div>
                      <p className="text-gray-400 text-sm line-clamp-2 mt-1">{video.description}</p>
                      <div className="mt-2 flex items-center gap-2">
                        <span className={`text-[10px] px-2 py-0.5 rounded font-bold uppercase ${
                          video.status === 'COMPLETED' ? 'bg-green-500/20 text-green-500' : 'bg-yellow-500/20 text-yellow-500'
                        }`}>
                          {video.status}
                        </span>
                        {video.minimumSubscriptionTier !== 'FREE' && (
                          <span className="text-[10px] px-2 py-0.5 rounded font-bold uppercase bg-primary/20 text-primary">
                            {video.minimumSubscriptionTier}
                          </span>
                        )}
                      </div>
                    </div>
                  </div>
                );
              })}
            </div>
          )}
        </section>
      </div>
    </>
  );
};

// Protected Route Component
const ProtectedRoute = ({ 
  children, 
  requiredRole 
}: { 
  children: React.ReactNode, 
  requiredRole?: string 
}) => {
  const { isAuthenticated, role } = useSelector((state: RootState) => state.auth);
  
  if (!isAuthenticated) {
    return <Navigate to="/login" />;
  }

  if (requiredRole && role !== requiredRole) {
    return <Navigate to="/" />;
  }

  return <>{children}</>;
};

function App() {
  return (
    <Router>
      <div className="min-h-screen bg-background text-white">
        <Routes>
          <Route path="/" element={<Landing />} />
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />
          <Route 
            path="/home" 
            element={
              <ProtectedRoute>
                <Home />
              </ProtectedRoute>
            } 
          />
          <Route 
            path="/upload" 
            element={
              <ProtectedRoute requiredRole="ADMIN">
                <Upload />
              </ProtectedRoute>
            } 
          />
          <Route 
            path="/subscribe" 
            element={
              <ProtectedRoute>
                <Subscribe />
              </ProtectedRoute>
            } 
          />
          <Route 
            path="/watch/:id" 
            element={
              <ProtectedRoute>
                <Watch />
              </ProtectedRoute>
            } 
          />
        </Routes>
      </div>
    </Router>
  );
}

export default App;
