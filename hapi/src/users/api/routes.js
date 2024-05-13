const usersRoutes = (handler) => [
  {
    method: 'POST',
    path: '/users',
    handler: handler.addUser,
  },
];

module.exports = usersRoutes;
