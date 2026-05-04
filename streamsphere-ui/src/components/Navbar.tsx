import { Link, useNavigate } from 'react-router-dom';
import { useDispatch, useSelector } from 'react-redux';
import { useEffect } from 'react';
import type { RootState } from '../app/store';
import { logout, setUserInfo } from '../features/auth/authSlice';
import { useGetUserProfileQuery } from '../features/auth/authApi';
import { LogOut, PlayCircle, Upload, Star } from 'lucide-react';
import Logo from './Logo';

const Navbar = () => {
  const { user, currentTier, role } = useSelector((state: RootState) => state.auth);
  const dispatch = useDispatch();
  const navigate = useNavigate();

  const { data: profile } = useGetUserProfileQuery(user as string, { skip: !user });

  useEffect(() => {
    if (profile) {
      dispatch(setUserInfo({ userId: profile.id, currentTier: profile.currentTier || 'FREE', role: profile.role }));
    }
  }, [profile, dispatch]);

  const handleLogout = () => {
    dispatch(logout());
    navigate('/login');
  };

  const isAdmin = role === 'ADMIN';

  return (
    <nav className="bg-black/90 backdrop-blur-md py-4 px-4 md:px-8 flex items-center justify-between sticky top-0 z-50 border-b border-white/5">
      <div className="flex items-center space-x-4 md:space-x-8">
        <Link to="/home" className="hover:opacity-90 transition-opacity">
          <Logo />
        </Link>
        <div className="hidden md:flex space-x-6 text-sm font-medium tracking-wide uppercase">
          <Link to="/home" className="text-white hover:text-primary transition duration-200">Home</Link>
          {isAdmin && (
            <Link to="/upload" className="text-white hover:text-primary transition duration-200">Upload</Link>
          )}
        </div>
      </div>

      <div className="flex items-center space-x-6">
        <div className="text-sm font-medium hidden sm:block text-gray-400">
          Logged in as: <span className="text-primary font-bold uppercase tracking-wider">{user}</span>
          <span className="ml-2 px-1.5 py-0.5 bg-white/5 border border-white/10 rounded text-[10px] uppercase">{role}</span>
        </div>
        
        <Link to="/subscribe" className="flex items-center gap-1 text-sm font-bold text-yellow-500 hover:text-yellow-400 bg-gray-900 px-3 py-1 rounded-full border border-yellow-600">
          <Star size={14} />
          {currentTier || 'FREE'}
        </Link>

        {isAdmin && (
          <Link to="/upload" className="text-white hover:text-primary transition p-2 bg-white/5 rounded-full border border-white/10">
            <Upload size={18} />
          </Link>
        )}
        <button
          onClick={handleLogout}
          className="text-white hover:text-primary transition flex items-center gap-2 text-sm"
        >
          <LogOut size={20} />
          <span className="hidden sm:inline">Logout</span>
        </button>
      </div>
    </nav>
  );
};

export default Navbar;
