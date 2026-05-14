import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  Box,
  Grid,
  Card,
  CardContent,
  Typography,
  LinearProgress,
  List,
  ListItem,
  ListItemText,
  ListItemIcon,
  Button,
} from '@mui/material';
import {
  People as PeopleIcon,
  TrendingUp as TrendingUpIcon,
  Business as BusinessIcon,
  Email as EmailIcon,
  Phone as PhoneIcon,
  Add as AddIcon,
} from '@mui/icons-material';
import { motion } from 'framer-motion';
import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, PieChart, Pie, Cell } from 'recharts';
import { toast } from 'react-toastify';
import { partnerAPI } from '../services/api';

const COLORS = ['#0088FE', '#00C49F', '#FFBB28', '#FF8042', '#8884d8'];

function Dashboard() {
  const navigate = useNavigate();
  const [partners, setPartners] = useState([]);
  const [loading, setLoading] = useState(true);
  const [stats, setStats] = useState({
    total: 0,
    withEmail: 0,
    withPhone: 0,
    withDic: 0,
  });

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    try {
      setLoading(true);
      const data = await partnerAPI.getAllPartners();
      setPartners(data);

      const stats = {
        total: data.length,
        withEmail: data.filter(p => p.email).length,
        withPhone: data.filter(p => p.phone).length,
        withDic: data.filter(p => p.dic).length,
      };
      setStats(stats);
    } catch (err) {
      toast.error('Chyba při načítání dat');
    } finally {
      setLoading(false);
    }
  };

  const StatCard = ({ title, value, icon, color, delay }) => (
    <Grid item xs={12} sm={6} md={3}>
      <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.3, delay }}
      >
        <Card sx={{ height: '100%' }}>
          <CardContent>
            <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
              <Box>
                <Typography color="text.secondary" gutterBottom variant="overline">
                  {title}
                </Typography>
                <Typography variant="h3" fontWeight={600}>
                  {loading ? '-' : value}
                </Typography>
              </Box>
              <Box
                sx={{
                  backgroundColor: `${color}20`,
                  borderRadius: 2,
                  p: 1.5,
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                }}
              >
                {React.cloneElement(icon, { sx: { fontSize: 40, color } })}
              </Box>
            </Box>
          </CardContent>
        </Card>
      </motion.div>
    </Grid>
  );

  const pieData = [
    { name: 'S emailem', value: stats.withEmail },
    { name: 'S telefonem', value: stats.withPhone },
    { name: 'S DIČ', value: stats.withDic },
    { name: 'Bez kontaktu', value: stats.total - stats.withEmail },
  ];

  const recentPartners = partners.slice(0, 5);

  return (
    <Box>
      <Box sx={{ mb: 4 }}>
        <Typography variant="h4" fontWeight={600} gutterBottom>
          Dashboard
        </Typography>
        <Typography variant="body1" color="text.secondary">
          Přehled partnerů a statistik
        </Typography>
      </Box>

      {loading ? (
        <LinearProgress sx={{ mb: 3 }} />
      ) : null}

      {/* Statistiky */}
      <Grid container spacing={3} sx={{ mb: 4 }}>
        <StatCard
          title="Celkem partnerů"
          value={stats.total}
          icon={<PeopleIcon />}
          color="#1976d2"
          delay={0}
        />
        <StatCard
          title="S emailem"
          value={stats.withEmail}
          icon={<EmailIcon />}
          color="#2e7d32"
          delay={0.1}
        />
        <StatCard
          title="S telefonem"
          value={stats.withPhone}
          icon={<PhoneIcon />}
          color="#ed6c02"
          delay={0.2}
        />
        <StatCard
          title="Plátci DPH"
          value={stats.withDic}
          icon={<TrendingUpIcon />}
          color="#9c27b0"
          delay={0.3}
        />
      </Grid>

      <Grid container spacing={3}>
        {/* Graf - sloupcový */}
        <Grid item xs={12} md={8}>
          <motion.div
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.3, delay: 0.4 }}
          >
            <Card>
              <CardContent>
                <Typography variant="h6" gutterBottom fontWeight={600}>
                  Přehled kontaktních údajů
                </Typography>
                <Box sx={{ height: 300, mt: 2 }}>
                  <ResponsiveContainer width="100%" height="100%">
                    <BarChart
                      data={[
                        { name: 'Email', value: stats.withEmail },
                        { name: 'Telefon', value: stats.withPhone },
                        { name: 'DIČ', value: stats.withDic },
                        { name: 'Celkem', value: stats.total },
                      ]}
                    >
                      <CartesianGrid strokeDasharray="3 3" />
                      <XAxis dataKey="name" />
                      <YAxis />
                      <Tooltip />
                      <Bar dataKey="value" fill="#1976d2" radius={[8, 8, 0, 0]} />
                    </BarChart>
                  </ResponsiveContainer>
                </Box>
              </CardContent>
            </Card>
          </motion.div>
        </Grid>

        {/* Koláčový graf */}
        <Grid item xs={12} md={4}>
          <motion.div
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.3, delay: 0.5 }}
          >
            <Card sx={{ height: '100%' }}>
              <CardContent>
                <Typography variant="h6" gutterBottom fontWeight={600}>
                  Rozložení údajů
                </Typography>
                <Box sx={{ height: 250, display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                  {stats.total > 0 ? (
                    <ResponsiveContainer width="100%" height="100%">
                      <PieChart>
                        <Pie
                          data={pieData}
                          cx="50%"
                          cy="50%"
                          innerRadius={60}
                          outerRadius={80}
                          fill="#8884d8"
                          paddingAngle={5}
                          dataKey="value"
                          label
                        >
                          {pieData.map((entry, index) => (
                            <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
                          ))}
                        </Pie>
                        <Tooltip />
                      </PieChart>
                    </ResponsiveContainer>
                  ) : (
                    <Typography color="text.secondary">Žádná data</Typography>
                  )}
                </Box>
              </CardContent>
            </Card>
          </motion.div>
        </Grid>

        {/* Poslední přidaní partneři */}
        <Grid item xs={12}>
          <motion.div
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.3, delay: 0.6 }}
          >
            <Card>
              <CardContent>
                <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 2 }}>
                  <Typography variant="h6" fontWeight={600}>
                    Poslední přidaní partneři
                  </Typography>
                  <Button
                    variant="contained"
                    startIcon={<AddIcon />}
                    onClick={() => navigate('/partner/new')}
                  >
                    Přidat nového
                  </Button>
                </Box>
                {recentPartners.length > 0 ? (
                  <List>
                    {recentPartners.map((partner, index) => (
                      <ListItem
                        key={partner.id}
                        sx={{
                          borderRadius: 2,
                          mb: 1,
                          '&:hover': {
                            backgroundColor: 'action.hover',
                            cursor: 'pointer',
                          },
                        }}
                        onClick={() => navigate(`/partner/${partner.id}`)}
                      >
                        <ListItemIcon>
                          <BusinessIcon color="primary" />
                        </ListItemIcon>
                        <ListItemText
                          primary={partner.companyName}
                          secondary={`IČO: ${partner.ico} ${partner.email ? `• ${partner.email}` : ''}`}
                        />
                      </ListItem>
                    ))}
                  </List>
                ) : (
                  <Typography color="text.secondary" textAlign="center" py={3}>
                    Zatím nemáte žádné partnery
                  </Typography>
                )}
              </CardContent>
            </Card>
          </motion.div>
        </Grid>
      </Grid>
    </Box>
  );
}

export default Dashboard;
