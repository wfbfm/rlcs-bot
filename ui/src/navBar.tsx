import React from 'react';
import {
    Box,
    Flex,
    Heading,
    Text,
    Button,
    useColorModeValue,
    Stack,
    useColorMode,
    Image,
    Icon,
    HStack
} from '@chakra-ui/react';
import { MoonIcon, SunIcon } from '@chakra-ui/icons'
import { IoRocketOutline } from "react-icons/io5";

export default function NavBar(): JSX.Element {
    const { colorMode, toggleColorMode } = useColorMode();
    const bannerColour = colorMode === 'light' ? 'linear(to-br, gray.100, gray.300)' : 'linear(to-tl, gray.700, gray.800)';

    return (
        <>
            <Box px={10} bgGradient={bannerColour}>
                <Flex h={14} alignItems='center' justifyContent='space-between'>
                    <Icon boxSize={12} as={IoRocketOutline}></Icon>
                    <Heading>
                        Rocket League Commentary Service
                    </Heading>
                    <Flex alignItems='center'>
                        <Stack direction='row' spacing={7}>
                            <Button onClick={toggleColorMode} bg={useColorModeValue('grey.500', 'grey.900')}>
                                {colorMode === 'light' ? <MoonIcon /> : <SunIcon />}
                            </Button>
                        </Stack>
                    </Flex>
                </Flex>
            </Box>
        </>
    );
}
