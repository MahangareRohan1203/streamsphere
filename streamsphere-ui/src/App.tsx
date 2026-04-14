import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { useSelector } from 'react-redux';
import { RootState } from './app/store';
import Login from './pages/Login';
import Register from './pages/Register';
import Upload from './pages/Upload';
import Navbar from './components/Navbar';

// Real Home Component
const Home = () => {
  return (
    <>
      <Navbar />
      <div className="p-8 max-w-7xl mx-auto">
        <header className="mb-12">
          <h1 className="text-5xl font-extrabold text-white mb-4">Unlimited movies, TV shows, and more.</h1>
          <p className="text-xl text-gray-400">Watch anywhere. Cancel anytime.</p>
        </header>

        <section>
          <h2 className="text-2xl font-bold mb-6">Recently Uploaded</h2>
          {/* Video Grid Placeholder */}
          <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-6">
            <div className="aspect-video bg-surface rounded-md border border-gray-800 flex items-center justify-center group cursor-pointer hover:scale-105 transition duration-300">
              <p className="text-gray-500 group-hover:text-primary transition">No videos yet. Upload one!</p>
            </div>
          </div>
        </section>
      </div>
    </>
  );
};

// Protected Route Component
const ProtectedRoute = ({ children }: { children: JSX.Element }) => {
  const { isAuthenticated } = useSelector((state: RootState) => state.auth);
  return isAuthenticated ? children : <Navigate to="/login" />;
};

function App() {
  return (
    <Router>
      <div className="min-h-screen bg-background text-white">
        <Routes>
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />
          <Route 
            path="/" 
            element={
              <ProtectedRoute>
                <Home />
              </ProtectedRoute>
            } 
          />
          <Route 
            path="/upload" 
            element={
              <ProtectedRoute>
                <Upload />
              </ProtectedRoute>
            } 
          />
        </Routes>
      </div>
    </Router>
  );
}

export default App;
