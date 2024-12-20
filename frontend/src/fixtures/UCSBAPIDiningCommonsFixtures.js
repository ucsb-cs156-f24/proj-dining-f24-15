export const diningCommonsFixtures = {
  oneDiningCommons: [
    {
      name: "Carrillo",
      code: "carrillo",
      hasSackMeal: false,
      hasTakeOutMeal: false,
      hasDiningCam: true,
      location: {
        latitude: 34.409953,
        longitude: -119.85277,
      },
    },
  ],

  threeDiningCommons: [
    {
      name: "Ortega",
      code: "ortega",
      hasSackMeal: true,
      hasTakeOutMeal: true,
      hasDiningCam: true,
      location: {
        latitude: 34.410987,
        longitude: -119.84709,
      },
    },
    {
      name: "De La Guerra",
      code: "dlg",
      hasSackMeal: false,
      hasTakeOutMeal: false,
      hasDiningCam: true,
      location: {
        latitude: 34.409811,
        longitude: -119.845026,
      },
    },
    {
      name: "Portola",
      code: "portola",
      hasSackMeal: true,
      hasTakeOutMeal: true,
      hasDiningCam: false, //Different then api returned value, but good for testing
      location: {
        latitude: 34.417723,
        longitude: -119.867427,
      },
    },
  ],
};

export default diningCommonsFixtures;
